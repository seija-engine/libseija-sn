package com.seija.sxml.compiler
import scala.util.Try
import com.seija.sxml.vm.{AltPattern, Alternative, CompiledFunction, CompiledModule, ImportInfo, Instruction, VMExpr, vmExprCastTo, Symbol as VMSymbol}
import com.seija.sxml.parser.{CExpr, TextSpan}

import scala.collection.mutable.ArrayBuffer
import com.seija.sxml.parser.LitValue
import com.seija.sxml.parser.SpanPos
import com.seija.sxml.vm.VMExpr.VMMap

import scala.collection.mutable.HashMap
import scala.collection.mutable
import scala.collection.mutable.Stack
import scala.collection.mutable.HashSet
import com.seija.sxml.vm.VMEnv

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
    
    def newStackVar(s:VMSymbol):Int = {
      val index = this.stackSize - 1
      this.stack.insert(s,index)
      index
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
    if(last.function.instructions.isEmpty) {
      last.function.instructions.addOne(Instruction.PushNil)
    }
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

case class LoopScope(
  val backInstrIdx:Int,
  val stackSize:Int,
  val argsCount:Int,
  var isRecur:Boolean = false,
  val isFN:Boolean = false
)

case class Compiler(vmEnv:VMEnv) {
  var loopScopeList:ArrayBuffer[LoopScope] = ArrayBuffer.empty
  val curModuleExportSet:mutable.HashSet[String] = mutable.HashSet.empty
  var curModName:String = "";

  def compileModule(module: TranslatorModule):Try[CompiledModule] = Try {
    val envs = FunctionEnvs()
    envs.startFunction(0,VMSymbol(None,""))
    this.handleModule(module,envs);
    
    for(expr <- module.exprList) {
      compileExpr(expr,envs,false).get
    }
    val endFunction = envs.endFunction()
    CompiledModule(module.imports.toArray,
                   module.exportSymbols.toArray,
                   endFunction.function)
  }

  def handleModule(module: TranslatorModule,envs:FunctionEnvs):Unit = {
    curModName = module.name;
    this.curModuleExportSet.clear();
    for(exportName <- module.exportSymbols) {
      this.curModuleExportSet += exportName;
    }
    
    val libSet:HashSet[String] = HashSet.from(module.imports.map(_.libName))
    //var index = 0;
    val globalSet:HashSet[(String,String)] = HashSet.empty
    for(expr <- module.exprList) {
      this.visitSymbols(expr.value,(symbol) => {
          if(symbol.ns.isEmpty) {
            this.vmEnv.getPreludeLibName(symbol.name).foreach {modName =>
              globalSet.add((modName,symbol.name))
              
            }
          }
          if(symbol.ns.isDefined && libSet.contains(symbol.ns.get)) {
            globalSet.add((symbol.ns.get,symbol.name))
          }
      })
    }
    globalSet.toArray.sorted.foreach { kv =>
       envs.current.emit(Instruction.LoadGlobal(kv._1,kv._2))
       if(this.vmEnv.getPreludeLibName(kv._2).isDefined) {
         envs.current.newStackVar(VMSymbol(None,kv._2))
       } else { envs.current.newStackVar(VMSymbol(Some(kv._1),kv._2)) }
       
    }
  }
  
  def visitSymbols(expr:VMExpr,symbolFn:(VMSymbol)=>Unit):Unit = {
    expr match
      case VMExpr.VMSymbol(value) => { symbolFn(value) }
      case VMExpr.VMArray(value) => { value.foreach(v => this.visitSymbols(v.value,symbolFn)) }
      case VMExpr.VMDef(_,expr) => this.visitSymbols(expr.value,symbolFn)
      case VMExpr.VMCall(fExpr,args) => {
        this.visitSymbols(fExpr.value,symbolFn)
        args.foreach(v => this.visitSymbols(v.value,symbolFn))
      }
      case VMExpr.VMMatch(value,alts) => {
        this.visitSymbols(value.value,symbolFn)
        for(alt <- alts) {
          this.visitSymbols(alt.expr.value,symbolFn)
        }
      }
      case VMExpr.VMMap(value) => { value.foreach(v => this.visitSymbols(v.value,symbolFn)) }
      case VMExpr.VMFunc(_,bodyList) => { bodyList.foreach(v => this.visitSymbols(v.value,symbolFn)) }
      case VMExpr.VMLet(lets, bodyLst, isLoop) => {
        lets.foreach(v => this.visitSymbols(v.value,symbolFn))
        bodyLst.foreach(v => this.visitSymbols(v.value,symbolFn))
      }
      case VMExpr.VMRecur(lst) => { lst.foreach(v => this.visitSymbols(v.value,symbolFn)) }
      case VMExpr.VMUnWrap(value) => this.visitSymbols(value.value,symbolFn)
      case VMExpr.VMXml(_,attrs,child) => {
        attrs.foreach(kv => this.visitSymbols(kv._2.value,symbolFn))
        child.foreach(v => this.visitSymbols(v.value,symbolFn))
      }
      case _ =>
  }

  def compileExpr(expr:TextSpan[VMExpr],envs:FunctionEnvs,isTail:Boolean):Try[Unit] = Try {
    expr.value match
      case VMExpr.VMNil => envs.current.emit(Instruction.PushNil)
      case VMExpr.VMLit(value) => this.compileLit(value,envs.current)
      case VMExpr.VMArray(value) => this.compileArray(value,envs).get
      case VMExpr.VMDef(name, expr) => this.compileDef(name,expr,envs).get
      case VMExpr.VMCall(fn, args) => this.compileCall(fn,args,envs).get
      case VMExpr.VMSymbol(value) => this.loadIdentifier(expr.pos, value, envs).get
      case VMExpr.VMMatch(value, alts) => this.compileMatch(value,alts,envs,isTail).get
      case VMExpr.VMKeyword(value, isLocal) => this.emitKeyworld(value,envs.current)
      case VMExpr.VMMap(value) => this.compileMap(expr.pos,value,envs).get
      case VMExpr.VMFunc(args, bodyLst) => this.compileFunc(expr.pos,args,bodyLst,envs).get
      case VMExpr.VMLet(lets, bodyLst, isLoop) => this.compileLet(expr.pos,lets,bodyLst,isLoop,envs).get
      case VMExpr.VMRecur(lst) => this.compileRecur(expr.pos,lst,envs,isTail).get
      case VMExpr.VMUnWrap(value) => {
        this.compileExpr(value,envs,isTail).get
        envs.current.emit(Instruction.UnWrap)
      }
      case VMExpr.VMXml(tag, attrs, child) => this.compileXML(expr.pos,tag,attrs,child,envs,isTail).get
      case _ => {  }

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
   
    val findVar = this.find(symbol,envs).getOrElse(throw NotFoundSymbol(pos,symbol.name))
    
    findVar match
      case FindVariable.Stack(index) => envs.current.emit(Instruction.Push(index))
      case FindVariable.UpVar(value) => envs.current.emit(Instruction.PushUpVar(value))
  }

  protected def compileXML(pos:SpanPos,tag:String,
                           attrs:Vector[(String,TextSpan[VMExpr])],
                           childs:Vector[TextSpan[VMExpr]], envs:FunctionEnvs,isTail:Boolean):Try[Unit] = Try {
     envs.current.emitString(tag)
     for((attrK,attrV) <- attrs) {
      envs.current.emitString(attrK)
      this.compileExpr(attrV,envs,isTail).get
     }
     for(child <- childs) {
      this.compileExpr(child,envs,isTail).get
     }
     envs.current.emit(Instruction.ConstructXML(attrs.length,childs.length))
  }

  protected def compileRecur(pos:SpanPos,args:Vector[TextSpan[VMExpr]],envs:FunctionEnvs,isTail:Boolean):Try[Unit] = Try {
    val loopScope = this.loopScopeList.last
    if(args.length != loopScope.argsCount || !isTail) throw InvalidRecur(pos)
    var curStackPos = loopScope.stackSize
    for(expr <- args) {
      this.compileExpr(expr,envs,false).get
    }
    envs.current.emit(Instruction.ReplaceTo(curStackPos,args.length))
    if(loopScope.isFN) {
      val popSize = loopScope.stackSize - args.length
      envs.current.emit(Instruction.Pop(popSize))
      envs.current.emit(Instruction.Jump(0))
      loopScope.isRecur = true
      envs.current.emit(Instruction.PushNil)
    } else {
      val curStack = envs.current.stackSize
      val popSize = curStack - loopScope.stackSize - args.length
      envs.current.emit(Instruction.Pop(popSize))
      envs.current.emit(Instruction.Jump(loopScope.backInstrIdx))
      loopScope.isRecur = true
      envs.current.emit(Instruction.PushNil)
    }
    
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
      var idx = 0;
      for(expr <- lst) {
        this.compileExpr(expr,envs,idx == lst.length - 1).get
        idx += 1
      }
      envs.current.exitScope()
      val endStackSize = envs.current.stackSize
      val popCount = endStackSize - curStackSize - 1
      envs.current.emit(Instruction.Slide(popCount))
    } else { //loop
      val curStackSize = envs.current.stackSize
      pushLetVars()
      val markInstr = envs.current.function.instructions.length
      this.putLoopScope(markInstr,curStackSize,lets.length / 2,false)
      for(idx <- lst.indices) {
        val curExpr = lst(idx)
        if(idx == lst.length - 1) {
          this.compileExpr(curExpr,envs,true).get
        } else {
          this.compileExpr(curExpr,envs,false).get
        }
      }
      val endStackSize = envs.current.stackSize
      val popCount = endStackSize - curStackSize - 1
      envs.current.emit(Instruction.Slide(popCount))
      envs.current.exitScope()
      this.popLoopScope()
    }
  }

  private def putLoopScope(backInstrIdx:Int,stackSize:Int,argsCount:Int,isFN:Boolean):Unit = {
    this.loopScopeList.addOne(LoopScope(backInstrIdx,stackSize,argsCount))
  }

  private def popLoopScope():Option[LoopScope] = {
    if(this.loopScopeList.isEmpty) None else {
      val last = this.loopScopeList.remove(this.loopScopeList.length - 1)
      Some(last)
    } 
  }

  protected def compileFunc(pos:SpanPos,args:Vector[VMSymbol],bodyLst:Vector[TextSpan[VMExpr]],envs:FunctionEnvs):Try[Unit] = Try {
    val stackStart = envs.current.stackSize
    
    envs.current.emit(Instruction.NewClosure(0,0))
    val offset = envs.current.function.instructions.length - 1
    envs.current.emit(Instruction.Push(stackStart))
    
    val markInstr = envs.current.function.instructions.length
    this.putLoopScope(0,stackStart,args.length,true)

    envs.startFunction(args.length,VMSymbol(None,""))
    envs.current.stack.enterScope()
    for(arg <- args) {
        envs.current.pushStackVar(arg)
    }

    var index = 0;
    for(expr <- bodyLst) {
        this.compileExpr(expr,envs,index == bodyLst.length - 1).get
        index += 1
    }
    envs.current.exitScope()
    this.popLoopScope()
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

  protected def compileMatch(value:TextSpan[VMExpr],alts:Vector[Alternative],envs:FunctionEnvs,isTail:Boolean):Try[Unit] = Try {
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
              envs.current.emit(Instruction.PushChar(if(value) '1' else '0'))
              envs.current.emit(Instruction.EQ)
            }
            case LitValue.LChar(value) => {
              envs.current.emit(Instruction.PushChar(value))
              envs.current.emit(Instruction.EQ)
            }
            case LitValue.LString(value) => {
              envs.current.emitString(value)
              envs.current.emit(Instruction.EQ)
            }
        }
        case AltPattern.Array(lst) => { 
          val lhsI = envs.current.stackSize - 1
          envs.current.emit(Instruction.Push(lhsI))
          this.compileArray(lst,envs)
          envs.current.emit(Instruction.EQ)
        }
        case AltPattern.Ident(ident) => {
          envs.current.emit(Instruction.PushChar('1'))
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
        case AltPattern.Array(_) => {
          val instrs = envs.current.function.instructions
          instrs.update(startIndex,Instruction.CJump(instrs.length))
        }
        case AltPattern.Ident(symbol) => {
          val instrs = envs.current.function.instructions
          instrs.update(startIndex,Instruction.CJump(instrs.length))
        }
      this.compileExpr(alt.expr,envs,isTail).get
      if(isTail && loopScopeList.nonEmpty && loopScopeList.last.isRecur) {
        loopScopeList.last.isRecur = false;
      } else {
        envs.current.emit(Instruction.Slide(1))
        endJumps.addOne(envs.current.function.instructions.length)
        envs.current.emit(Instruction.Jump(0))
      }
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
      this.compileExpr(vmExpr,envs:FunctionEnvs,false).get
    }
    envs.current.emit(Instruction.ConstructArray(list.length))
  }

  protected def compileDef(name:VMSymbol,expr:TextSpan[VMExpr],envs:FunctionEnvs):Try[Unit] = Try {
    this.compileExpr(expr,envs,false).get
    val index = envs.current.newStackVar(name)
    envs.current.emit(Instruction.AddGlobal(index,curModName,name.name))
  }

  protected def compileCall(name:TextSpan[VMExpr],args:Vector[TextSpan[VMExpr]],envs:FunctionEnvs):Try[Unit] = Try(scala.util.boundary{
    val opSymbol = vmExprCastTo[VMExpr.VMSymbol](name.value)
    if(opSymbol.isDefined) {
      if(this.tryCompilePrimitive(opSymbol.get.value,args,envs).get)  scala.util.boundary.break()
    }
    this.compileExpr(name,envs,false).get
    for(arg <- args) {
      this.compileExpr(arg,envs,false).get
    }
    envs.current.emitCall(args.length)
  })

  private def tryCompilePrimitive(symbol:VMSymbol,args:Vector[TextSpan[VMExpr]],envs:FunctionEnvs):Try[Boolean] = Try {
    var binTail:Option[Instruction] = None;
    val binOp = symbol.name match
      case "+" => Some(Instruction.Add)
      case "-" => Some(Instruction.Subtract)
      case "*" => Some(Instruction.Multiply)
      case "/" => Some(Instruction.Divide)
      case "=" => Some(Instruction.EQ)
      case "<" => Some(Instruction.LT)
      case ">" => Some(Instruction.GT)
      case ">=" => {
        binTail = Some(Instruction.Not)
        Some(Instruction.LT)
      }
      case "<=" => {
        binTail = Some(Instruction.Not)
        Some(Instruction.GT)
      }
      case _   => None
    val op = symbol.name match
      case "!" => Some(Instruction.Not)
      case _ => None
    if(binOp.isDefined) {
      this.compileExpr(args(0),envs,false).get
      this.compileExpr(args(1),envs,false).get
      envs.current.emit(binOp.get)
      if(binTail.isDefined) {
        envs.current.emit(binTail.get)
      }
      true
    } else if(op.isDefined) {
      this.compileExpr(args(0),envs,false).get
      envs.current.emit(op.get)
      true
    } else {
      false
    }
  }

  private def emitKeyworld(value:String,env:FunctionEnv):Unit = {
    val index = env.addStringConst(value.tail)
    env.emit(Instruction.PushKW(index))
  }
}