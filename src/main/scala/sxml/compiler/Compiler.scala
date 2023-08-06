package sxml.compiler
import scala.util.Try
import sxml.vm.{CompiledModule, VMExpr,Symbol as VMSymbol}
import sxml.parser.{CExpr, TextSpan}
import sxml.vm.{CompiledFunction,Instruction}
import scala.collection.mutable.ArrayBuffer
import sxml.parser.LitValue
import sxml.parser.SpanPos
import sxml.vm.Alternative
import scala.util.Success
import scala.util.boundary
import sxml.vm.AltPattern
import scala.collection.mutable.HashMap

case class FunctionEnv(val function:CompiledFunction) {
    val stack:ScopedMap[VMSymbol,Int] = ScopedMap()
    val freeVars:ArrayBuffer[VMSymbol] = ArrayBuffer.empty
    var stackSize:Int = 0
    var maxStackSize:Int = 0

    def emit(instruction:Instruction):Unit = {
      val adjustment = instruction.adjust()
      if(adjustment > 0) {
        this.increaseStack(adjustment)
      } else {
        this.stackSize += adjustment
      }
      this.function.instructions.addOne(instruction)
    }

    def increaseStack(count:Int):Unit = {
      this.stackSize += count
      this.maxStackSize = this.stackSize.max(this.maxStackSize)
    }

    def addStringConst(value:String):Int = {
      val findIndex = this.function.strings.indexOf(value)
      if(findIndex > 0) findIndex else {
        this.function.strings.addOne(value)
        this.function.strings.length - 1
      }
    }

    def emitString(value:String):Unit = {
      val index = this.addStringConst(value)
      this.emit(Instruction.PushString(index))
    }

    def newStackVar(s:VMSymbol):Unit = {
      val index = this.stackSize - 1
      this.stack.insert(s,index)
    }

    def exitScope():Int = { this.stack.exitScope() }
}

object FunctionEnv {
  def apply(args:Int,id:VMSymbol):FunctionEnv = {
    FunctionEnv(CompiledFunction(args,id,ArrayBuffer(),ArrayBuffer()))
  }
}

case class FunctionEnvs(envs:ArrayBuffer[FunctionEnv] = ArrayBuffer.empty) {
  def startFunction(args:Int,id:VMSymbol):Unit = {
    this.envs.addOne(FunctionEnv(args,id))
  }

  def endFunction():FunctionEnv = {
    val last = this.envs.remove(this.envs.length - 1)
    last.function.instructions.addOne(Instruction.Return)
    last
  }

  def current:FunctionEnv = this.envs.last

  def head:FunctionEnv = this.envs.head

}

enum FindVariable[G] {
  case Stack(index:Int)
  case UpVar(value:G)
}

class Compiler {
  var curModuleKeyworld:ArrayBuffer[String] = ArrayBuffer.empty
  def compileModule(module: TranslatorModule):Try[CompiledModule] = Try {
    val envs = FunctionEnvs()
    envs.startFunction(0,VMSymbol(None,""))
    for(expr <- module.exprList) {
      compileExpr(expr,envs)
    }
    val endFunction = envs.endFunction()
    CompiledModule(endFunction.freeVars.toArray,endFunction.function)
  }

  def compileExpr(expr:TextSpan[VMExpr],envs:FunctionEnvs):Try[Unit] = Try {
    expr.value match
      case VMExpr.VMNil => envs.current.emit(Instruction.PushNil)
      case VMExpr.VMLit(value) => this.compileLit(value,envs.current)
      case VMExpr.VMArray(value) => this.compileArray(value,envs).get
      case VMExpr.VMDef(name, expr) => this.compileDef(name,expr,envs).get
      case VMExpr.VMCall(fn, args) => this.compileCall(fn,args,envs).get
      case VMExpr.VMSymbol(value) => this.loadIdentifier(expr.pos,value,envs).get
      case VMExpr.VMMatch(value, alts) => this.compileMatch(value,alts,envs).get
      case VMExpr.VMKeyworld(value, isLocal) => this.emitKeyworld(value,envs.current)
      case VMExpr.VMMap(value) => this.compileMap(expr.pos,value,envs)
      case VMExpr.VMFunc(args, bodyLst) =>
      case VMExpr.VMLet(lets, bodyLst, isLoop) =>
      case VMExpr.VMRecur(lst) =>
      case VMExpr.VMXml(tag, attrs, child) =>
      case VMExpr.VMUnWrap(value) =>
  }

  protected def find(symbol:VMSymbol,envs:FunctionEnvs):Option[FindVariable[Int]] = {
    val stackSymbol = envs.current.stack.get(symbol)
    if(stackSymbol.isDefined) {
      return stackSymbol.map(idx => FindVariable.Stack(idx))
    }
    None
  }

  protected def loadIdentifier(pos:SpanPos,symbol:VMSymbol,envs:FunctionEnvs):Try[Unit] = Try {
    val findVar = this.find(symbol,envs).getOrElse(throw NotFoundSymbol(pos))
    findVar match
      case FindVariable.Stack(index) => envs.current.emit(Instruction.Push(index))
      case FindVariable.UpVar(value) => ???    
  }

  protected def compileMap(pos:SpanPos,list:Vector[TextSpan[VMExpr]],envs:FunctionEnvs):Try[Unit] = Try {
     if(list.length % 2 != 0) throw ErrMapCount(pos)
     for(idx <- 0.until(list.length,2)) {
      this.compileExpr(list(idx),envs).get
      this.compileExpr(list(idx + 1),envs).get
     }
     envs.current.emit(Instruction.ConstructMap(list.length / 2))
  }

  protected def compileMatch(value:TextSpan[VMExpr],alts:Vector[Alternative],envs:FunctionEnvs):Try[Unit] = Try {
    this.compileExpr(value,envs).get
    val startJumps:ArrayBuffer[Int] = ArrayBuffer.empty
    for(alt <- alts) {
      alt.pattern match
        case AltPattern.Literal(value) => {
          val lhsI = envs.current.stackSize - 1
          envs.current.emit(Instruction.Push(lhsI))
          value match
            case LitValue.LLong(value) => {
              envs.current.emit(Instruction.PushInt(value))
              envs.current.emit(Instruction.EQ)
            }
            case LitValue.LFloat(value) => {
              envs.current.emit(Instruction.PushFloat(value))
              envs.current.emit(Instruction.EQ)
            }
            case LitValue.LBool(value) => {
              envs.current.emit(Instruction.PushChar(if(value) '1' else '0'))
              envs.current.emit(Instruction.CharEQ)
            }
            case LitValue.LChar(value) => {
              envs.current.emit(Instruction.PushChar(value))
              envs.current.emit(Instruction.CharEQ)
            }
            case LitValue.LString(value) => {
              envs.current.emitString(value)
              envs.current.emit(Instruction.StringEQ)
            }
        }
      startJumps.addOne(envs.current.function.instructions.length)
      envs.current.emit(Instruction.CJump(0))
    }
    val endJumps:ArrayBuffer[Int] = ArrayBuffer.empty
    for ((alt,startIndex) <- alts.zip(startJumps)) {
      envs.current.stack.enterScope()
      alt.pattern match
        case AltPattern.Literal(value) => {
          val instrs = envs.current.function.instructions;
          instrs.update(startIndex,Instruction.CJump(instrs.length))
        }
      this.compileExpr(alt.expr,envs).get
      val exitCount = envs.current.exitScope()
      envs.current.emit(Instruction.Slide(exitCount))
      endJumps.addOne(envs.current.function.instructions.length)
      envs.current.emit(Instruction.Jump(0))
    }
    for(index <- endJumps) {
      val instr = envs.current.function.instructions;
      instr.update(index,Instruction.Jump(instr.length))
    }
  }

  protected def compileLit(value:LitValue,env:FunctionEnv):Unit = {
    value match
        case LitValue.LLong(value) => env.emit(Instruction.PushInt(value))
        case LitValue.LFloat(value) => env.emit(Instruction.PushFloat(value))
        case LitValue.LChar(value) => env.emit(Instruction.PushChar(value))
        case LitValue.LBool(value) => env.emit(Instruction.PushChar(if(value) '1' else '0'))
        case LitValue.LString(value) => env.emitString(value)
  }

  protected def compileArray(list:Vector[TextSpan[VMExpr]],envs:FunctionEnvs):Try[Unit] = Try {
    list.foreach { vmExpr =>
      this.compileExpr(vmExpr,envs:FunctionEnvs)
    }
    envs.current.emit(Instruction.ConstructArray(list.length))
  }

  protected def compileDef(name:VMSymbol,expr:TextSpan[VMExpr],envs:FunctionEnvs):Try[Unit] = Try {
    this.compileExpr(expr,envs).get
    envs.current.newStackVar(name)
  }

  protected def compileCall(name:TextSpan[VMExpr],args:Vector[TextSpan[VMExpr]],envs:FunctionEnvs):Try[Unit] = Try(boundary{
    name.value match
      case VMExpr.VMSymbol(value) => {
        if(this.tryCompilePrimitive(value,args,envs).get) boundary.break()
      }
      case _ => {

      }
  })

  private def tryCompilePrimitive(symbol:VMSymbol,args:Vector[TextSpan[VMExpr]],envs:FunctionEnvs):Try[Boolean] = Try {
    val binOp = symbol.name match
      case "+" => Some(Instruction.Add)
      case "-" => Some(Instruction.Subtract)
      case "*" => Some(Instruction.Multiply)
      case "/" => Some(Instruction.Divide)
      case "=" => Some(Instruction.EQ)
      case "<" => Some(Instruction.LT)
      case ">" => Some(Instruction.GT)
      case _   => None
    binOp match
      case None => false
      case Some(op) => {
        this.compileExpr(args(0),envs)
        this.compileExpr(args(1),envs)
        envs.current.emit(op)
        true
      }
  }


  private def emitKeyworld(value:String,env:FunctionEnv):Unit = {
    val keyIndex = this.curModuleKeyworld.indexOf(value)
    if(keyIndex >= 0) {
      env.emit(Instruction.PushKW(keyIndex))
    }
    this.curModuleKeyworld.addOne(value)
    env.emit(Instruction.PushKW(this.curModuleKeyworld.length - 1))
  }
}