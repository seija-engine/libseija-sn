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
import sxml.vm.AltPattern
import scala.collection.mutable.HashMap
import sxml.vm.vmExprCastTo
import scala.collection.mutable.Stack

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

    def emitCall(args:Int,tailPos:Boolean = false):Unit = {
      this.emit(Instruction.Call(args))
    }
    
    def newStackVar(s:VMSymbol):Unit = {
      val index = this.stackSize - 1
      //println(s"new stackVar:${s} = ${index}")
      this.stack.insert(s,index)
    }

    def pushStackVar(s:VMSymbol):Unit = {
        this.increaseStack(1)
        this.newStackVar(s)
    }

    def upVar(s:VMSymbol):Int = {
      val index = this.freeVars.indexOf(s);
      if(index >= 0) { index } else {
        this.freeVars.addOne(s)
        this.freeVars.length - 1
      }
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

case class LoopScope(val backInstrIdx:Int,val stackSize:Int,val argsCount:Int)

class Compiler {
  var curModuleKeyword:ArrayBuffer[String] = ArrayBuffer.empty
  var loopScopeList:Stack[LoopScope] = Stack.empty

  def compileModule(module: TranslatorModule):Try[CompiledModule] = Try {
    val envs = FunctionEnvs()
    envs.startFunction(0,VMSymbol(None,""))
    for(expr <- module.exprList) {
      compileExpr(expr,envs,false).get
    }
    val endFunction = envs.endFunction()
    CompiledModule(endFunction.freeVars.toArray,endFunction.function)
  }

  def compileExpr(expr:TextSpan[VMExpr],envs:FunctionEnvs,isTail:Boolean):Try[Unit] = Try {
    expr.value match
      case VMExpr.VMNil => envs.current.emit(Instruction.PushNil)
      case VMExpr.VMLit(value) => this.compileLit(value,envs.current)
      case VMExpr.VMArray(value) => this.compileArray(value,envs).get
      case VMExpr.VMDef(name, expr) => this.compileDef(name,expr,envs).get
      case VMExpr.VMCall(fn, args) => this.compileCall(fn,args,envs).get
      case VMExpr.VMSymbol(value) => this.loadIdentifier(expr.pos,value,envs).get
      case VMExpr.VMMatch(value, alts) => this.compileMatch(value,alts,envs).get
      case VMExpr.VMKeyworld(value, isLocal) => this.emitKeyworld(value,envs.current)
      case VMExpr.VMMap(value) => this.compileMap(expr.pos,value,envs).get
      case VMExpr.VMFunc(args, bodyLst) => this.compileFunc(expr.pos,args,bodyLst,envs).get
      case VMExpr.VMLet(lets, bodyLst, isLoop) => this.compileLet(expr.pos,lets,bodyLst,isLoop,envs).get
      case VMExpr.VMRecur(lst) => this.compileRecur(expr.pos,lst,envs).get
      case VMExpr.VMXml(tag, attrs, child) =>
      case VMExpr.VMUnWrap(value) =>
  }

  protected def find(symbol:VMSymbol,envs:FunctionEnvs):Option[FindVariable[Int]] = {
    val stackSymbol = envs.current.stack.get(symbol)
    //in stack
    if(stackSymbol.isDefined) {
      return stackSymbol.map(idx => FindVariable.Stack(idx))
    }
    //upvars
    val i = envs.envs.length - 1
    for(idx <- i.to(0,-1)) {
      val curEnv = envs.envs(idx)
      val findVar = curEnv.stack.get(symbol)
      if(findVar.isDefined) {
        val index = envs.current.upVar(symbol)
        return Some(FindVariable.UpVar(index))
      }
    }
    None
  }

  protected def loadIdentifier(pos:SpanPos,symbol:VMSymbol,envs:FunctionEnvs):Try[Unit] = Try {
    val findVar = this.find(symbol,envs).getOrElse(throw NotFoundSymbol(pos))
    findVar match
      case FindVariable.Stack(index) => envs.current.emit(Instruction.Push(index))
      case FindVariable.UpVar(value) => envs.current.emit(Instruction.PushUpVar(value))
  }

  protected def compileRecur(pos:SpanPos,args:Vector[TextSpan[VMExpr]],envs:FunctionEnvs):Try[Unit] = Try {
    val loopScope = this.loopScopeList.headOption.getOrElse(throw InvalidRecur(pos))
    if(args.length != loopScope.argsCount) throw InvalidRecur(pos)
    val curStack = envs.current.stackSize
    val popSize = curStack - loopScope.stackSize - loopScope.argsCount
    println(s"${curStack} ${loopScope.stackSize} ${args.length}")
    var curStackPos = loopScope.stackSize
    for(expr <- args) {
      this.compileExpr(expr,envs,false).get
      envs.current.emit(Instruction.ReplaceTo(curStackPos))
      curStackPos += 1
    }
    envs.current.emit(Instruction.Pop(popSize))
  }

  protected def compileLet(pos:SpanPos,lets:Vector[TextSpan[VMExpr]],lst:Vector[TextSpan[VMExpr]],isLoop:Boolean,envs:FunctionEnvs):Try[Unit] = Try {
    if(lets.length % 2 != 0) throw InvalidLet(pos)
    def pushLetVars():Unit = {
      for(idx <- 0.until(lets.length,2)) {
        val symbol = vmExprCastTo[VMExpr.VMSymbol](lets(idx).value).getOrElse(throw InvalidLet(pos))
        val valueExpr = lets(idx + 1)
        this.compileExpr(valueExpr,envs,false).get
        envs.current.newStackVar(symbol.value)
      }
    }
    envs.current.stack.enterScope()
    //let 
    if(!isLoop) {
      val curStackSize = envs.current.stackSize
      pushLetVars()
      for(expr <- lst) {
        this.compileExpr(expr,envs,false).get
      }
      envs.current.exitScope()
      val endStackSize = envs.current.stackSize
      val popCount = endStackSize - curStackSize - 1
      envs.current.emit(Instruction.Slide(popCount))
    } else { //loop
      val curStackSize = envs.current.stackSize
      val markInstr = envs.current.function.instructions.length
      this.putLoopScope(markInstr,curStackSize,lets.length / 2)
      pushLetVars()
      for(idx <- lst.indices) {
        val curExpr = lst(idx)
        if(idx == lst.length - 1) {
          this.compileExpr(curExpr,envs,true).get
        } else {
          this.compileExpr(curExpr,envs,false).get
        }
      }
      this.popLoopScope()
    }
  }

  private def putLoopScope(backInstrIdx:Int,stackSize:Int,argsCount:Int):Unit = {
    this.loopScopeList.push(LoopScope(backInstrIdx,stackSize,argsCount))
  }

  private def popLoopScope():Option[LoopScope] = {
    if(this.loopScopeList.isEmpty) None else Some(this.loopScopeList.pop())
  }

  protected def compileFunc(pos:SpanPos,args:Vector[VMSymbol],bodyLst:Vector[TextSpan[VMExpr]],envs:FunctionEnvs):Try[Unit] = Try {
    val stackStart = envs.current.stackSize
    envs.current.emit(Instruction.NewClosure(0,0))
    val offset = envs.current.function.instructions.length - 1
    envs.current.emit(Instruction.Push(stackStart))

    envs.startFunction(args.length,VMSymbol(None,""))
    envs.current.stack.enterScope()
    for(arg <- args) {
        envs.current.pushStackVar(arg)
    }
    for(expr <- bodyLst) {
        this.compileExpr(expr,envs,false).get
    }
    envs.current.exitScope()
    val f = envs.endFunction()
    for(freeVar <- f.freeVars) {
      this.find(freeVar,envs).get match
        case FindVariable.Stack(index) => {
          envs.current.emit(Instruction.Push(index))
        }
        case FindVariable.UpVar(index) => {
          envs.current.emit(Instruction.PushUpVar(index))
        }
    }
    val functionIndex:Int = envs.current.function.innerFunctions.length
    envs.current.function.instructions.update(offset,Instruction.NewClosure(functionIndex,f.freeVars.length))
    envs.current.emit(Instruction.CloseClosure(f.freeVars.length))
    envs.current.stackSize -= f.freeVars.length
    envs.current.function.innerFunctions.addOne(f.function)
    
  }

  protected def compileMap(pos:SpanPos,list:Vector[TextSpan[VMExpr]],envs:FunctionEnvs):Try[Unit] = Try {
     if(list.length % 2 != 0) throw ErrMapCount(pos)
     for(idx <- 0.until(list.length,2)) {
      this.compileExpr(list(idx),envs,false).get
      this.compileExpr(list(idx + 1),envs,false).get
     }
     envs.current.emit(Instruction.ConstructMap(list.length / 2))
  }

  protected def compileMatch(value:TextSpan[VMExpr],alts:Vector[Alternative],envs:FunctionEnvs):Try[Unit] = Try {
    this.compileExpr(value,envs,false).get
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
              //envs.current.emit(Instruction.PushChar(if(value) '1' else '0'))
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
      alt.pattern match
        case AltPattern.Literal(_) => {
          val instrs = envs.current.function.instructions
          instrs.update(startIndex,Instruction.CJump(instrs.length))
        }
      this.compileExpr(alt.expr,envs,false).get
      envs.current.emit(Instruction.Slide(1))
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
      this.compileExpr(vmExpr,envs:FunctionEnvs,false)
    }
    envs.current.emit(Instruction.ConstructArray(list.length))
  }

  protected def compileDef(name:VMSymbol,expr:TextSpan[VMExpr],envs:FunctionEnvs):Try[Unit] = Try {
    this.compileExpr(expr,envs,false).get
    envs.current.newStackVar(name)
  }

  protected def compileCall(name:TextSpan[VMExpr],args:Vector[TextSpan[VMExpr]],envs:FunctionEnvs):Try[Unit] = Try(scala.util.boundary{
    val opSymbol = vmExprCastTo[VMExpr.VMSymbol](name.value)
    if(opSymbol.isDefined) {
      if(this.tryCompilePrimitive(opSymbol.get.value,args,envs).get)  scala.util.boundary.break()
    }
    this.compileExpr(name,envs,false).get
    for(arg <- args) {
      this.compileExpr(arg,envs,false)
    }
    envs.current.emitCall(args.length)
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
        this.compileExpr(args(0),envs,false)
        this.compileExpr(args(1),envs,false)
        envs.current.emit(op)
        true
      }
  }

  private def emitKeyworld(value:String,env:FunctionEnv):Unit = {
    val keyIndex = this.curModuleKeyword.indexOf(value)
    if(keyIndex >= 0) {
      env.emit(Instruction.PushKW(keyIndex))
    }
    this.curModuleKeyword.addOne(value)
    env.emit(Instruction.PushKW(this.curModuleKeyword.length - 1))
  }
}