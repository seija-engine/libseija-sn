package sxml.vm
import scala.collection.mutable.{Stack,HashMap as MHashMap}
import sxml.vm.VMValue
import scala.util.Try
import scala.math.Numeric.DoubleIsFractional
import scala.math.Ordering.LongOrdering
import scala.collection.immutable.HashMap
import scala.collection.mutable.ArrayBuffer

class VMStack {
   val values:Stack[VMValue] = Stack.empty
   val frames:Stack[VMCallStack] = Stack.empty

   def apply(index:Int):VMValue = this.values(index)

   def enterCallStack(offset:Int,state:ClosureState):VMCallStack = {
      val callStack = VMCallStack(offset,this,state)
      this.frames.addOne(callStack)
      callStack
   }

   def exitCallStack():Option[VMCallStack] = {
      this.frames.remove(this.frames.length - 1)
      this.frames.lastOption
   }
}



case class ClosureState(
   val closure:ClosureData,
   var instructionIndex:Int)

class VMCallStack(offsetValue:Int,stackRef:VMStack,stateValue:ClosureState) {
   val stack:VMStack = stackRef
   val offset:Int = offsetValue
   val state:ClosureState = stateValue
   var curIndex:Int = 0

   def execute_(vm:SXmlVM):Try[Option[VMCallStack]] = Try {
      this.curIndex = this.state.instructionIndex
      var isRun = true
      var nextStack:Option[VMCallStack] = None;
      while(isRun) {
         val instr = this.instruction()
         val index = this.curIndex
         this.step()
         instr match
            case Instruction.PushNil => { this.push(VMValue.NIL()) }
            case Instruction.PushInt(value) => { this.push(VMValue.VMLong(value)) }
            case Instruction.PushChar(value) => {  this.push(VMValue.VMChar(value)) }
            case Instruction.PushFloat(value) => { this.push(VMValue.VMFloat(value)) }
            case Instruction.PushString(value) => {
               val str = this.state.closure.function.strings(value)
               this.push(VMValue.VMString(str))
            }
            case Instruction.PushKW(value) => {
               val str = this.state.closure.function.strings(value)
               this.push(VMValue.VMKeyword(str))  
            }
            case Instruction.Push(idx) => {
               val value = this.get(idx)
               this.push(value)
            }
            case Instruction.LoadGlobal(lib,name) => {
               this.push(vm.env.getModuleVar(lib,name))
            }
            case Instruction.ConstructArray(count) => {
               val takeList = this.takeTail(count)
               this.push(VMValue.VMArray(takeList.toVector))
            }
            case Instruction.ConstructMap(count) => {
               val rmCount = count * 2
               val takeList = this.takeTail(rmCount)
               val pushMap:HashMap[VMValue,VMValue] = HashMap.from(0.until(takeList.length,2)
                                                             .map(idx => (takeList(idx),takeList(idx+1))))
               this.push(VMValue.VMMap(pushMap))
            }
            case Instruction.NewClosure(cidx, upvars) => {
               val func = this.state.closure.function.innerFunctions(cidx)
               val lst:ArrayBuffer[VMValue] = ArrayBuffer.from(0.until(upvars).map(_ =>VMValue.NIL()))
               this.push(VMValue.VMClosure(ClosureData(func, lst))) 
            }
            case Instruction.CloseClosure(count) => {
               val i = this.len - count - 1;
               val closure = this.get(i).unwrap[VMValue.VMClosure]().get.data
               val start = this.len - closure.upvars.length;
               for(idx <- 0.until(closure.upvars.length)) {
                 closure.upvars.update(idx,this.get(start + idx))
               }
               val pop = closure.upvars.length + 1;
               this.popMany(pop)
            }
            case Instruction.PushUpVar(idx) => {
               val upVar = this.state.closure.upvars(idx)
               this.push(upVar)
            }
            case Instruction.Call(argsCount) => {
               this.state.instructionIndex = this.curIndex;
               val functionIndex = this.stack.values.length - 1 - argsCount;
               val fnValue = this.stack.values(functionIndex).unwrap[VMValue.VMClosure]().get;
               nextStack = Some(this.enterClosure(fnValue.data))
               isRun = false;
            }
            case Instruction.CJump(index) => {
               val curValue = this.pop();
               val isJump = curValue.unwrap[VMValue.VMChar]().map(_.value == '1').getOrElse(false)
               if(isJump) {
                  this.curIndex = index;
               }
            }
            case Instruction.Jump(index) => {
               this.curIndex = index;
            }
            case Instruction.Slide(count) => {
               this.slide(count)
            }
            case Instruction.Pop(count) => {
               if(count > 0) { this.popMany(count) }
            }
            case Instruction.ReplaceTo(idx,count) => {
               var curCount = count - 1;
               while(curCount >= 0) {
                  val popValue = this.pop()
                  this.stack.values.update(this.offset + idx + curCount,popValue)
                  curCount = curCount - 1
               }
               
            }
            case Instruction.ConstructXML(attrCount, childCount) => {
               this.constructXml(attrCount,childCount);
            }
            case Instruction.UnWrap => {
               val vmArray = this.pop().unwrap[VMValue.VMArray]().get;
               this.push(VMValue.VMUnWrap(vmArray.value))
            }
            case Instruction.EQ => {
               val rhs = this.pop()
               val lhs = this.pop()
               this.pushBoolean(rhs.equals(lhs))
               
            }
            case Instruction.Add | Instruction.Subtract | Instruction.Multiply |
                 Instruction.Divide | Instruction.LT | Instruction.GT => {
                  this.binNumberOp(instr)
            }
            case Instruction.Not => {
               val bValue = this.pop().unwrap[VMValue.VMChar]().map(_.value == '1').getOrElse(false)
               this.pushBoolean(!bValue)
            }
            case Instruction.Return => {
               isRun = false
            }
            case Instruction.AddGlobal(index, lib, name) => {
               vm.env.addModuleVar(lib,name,this.get(index))
            }
            
            
      }
      if(nextStack.isEmpty) {
         
         val slideLen = this.len
         this.slide(slideLen)
         nextStack = this.exitScope()
      }
      
      nextStack
   }

   def get(index:Int):VMValue =  this.stack.values(this.offset + index)

   def push(value:VMValue):Unit = this.stack.values.addOne(value)

   def pop():VMValue = this.stack.values.remove(this.stack.values.length - 1)

   def popMany(count:Int):Unit = {
      if(count <= 0) return
      this.stack.values.remove(this.stack.values.length - count,count)
   }

   def len:Int = this.stack.values.length - this.offset

   def slide(count:Int):Unit = {
     val lastIndex = this.stack.values.length - 1;
     val i = lastIndex - count;
     this.stack.values.update(i,this.stack.values(lastIndex))
     this.popMany(count)
   }
    
   def takeTail(count:Int):Stack[VMValue] = {
      val retList = this.stack.values.slice(this.stack.values.length - count,this.stack.values.length)
      this.stack.values.remove(this.stack.values.length - count,count)
      retList
   }

   def enterClosure(closure:ClosureData):VMCallStack = {
      this.stack.enterCallStack(this.stack.values.length - closure.function.args,ClosureState(closure,0))
   }

   def exitScope():Option[VMCallStack] = {
      this.stack.exitCallStack()
   }

   def pushBoolean(b:Boolean):Unit = {
      this.stack.values.addOne(VMValue.VMChar( if(b) '1' else '0'))
   }
   
   inline def binNumberOp(instr:Instruction):Unit = {
      val rhs = this.pop()
      val lhs = this.pop()
      val toFloat = lhs.isFloat() || rhs.isFloat()
      if(toFloat) {
         val lv:Double = lhs.castFloat().get
         val rv:Double = rhs.castFloat().get
         if(instr == Instruction.Divide) {
            this.stack.values.addOne(VMValue.VMFloat(lv / rv))
         } else {
            this.numberOp(lv,rv,instr,(value) => { 
               this.stack.values.addOne(VMValue.VMFloat(value))
            })
         }
      } else {
         val lv = lhs.castInt().get
         val rv = rhs.castInt().get
         if(instr == Instruction.Divide) {
            this.stack.values.addOne(VMValue.VMLong(lv / rv))
         } else {
            this.numberOp(lv,rv,instr,(value) => {
               this.stack.values.addOne(VMValue.VMLong(value))
            })
         }
      }
   }

   inline def numberOp[T](l:T,r:T,instr:Instruction,nf:(value:T)=> Unit)(using ord:Ordering[T])(using op:Numeric[T]):Unit = {
      import op._
      
      instr match
         case Instruction.Add => nf(l + r)
         case Instruction.Subtract => nf(l - r)
         case Instruction.Multiply => nf(l * r)
         case Instruction.LT => pushBoolean(ord.lt(l,r))
         case Instruction.GT => pushBoolean(ord.gt(l,r))
         case Instruction.EQ => pushBoolean(ord.equiv(l,r))
         case _ => op.zero
   }

   def instruction():Instruction = {
      this.state.closure.function.instructions(this.curIndex)
   }

   def step():Unit = {
      this.curIndex += 1
   }

   def jump(index:Int):Unit = {
      this.curIndex = index
   }

   def constructXml(attrCount:Int,childCount:Int):Unit = {
     val childList = this.stack.values.reverse.take(childCount)
     var realChildList:Stack[VMValue] = Stack.empty
     for(item <- childList) {
       item match
         case VMValue.VMUnWrap(value) => {
            realChildList.addAll(value)
         }
         case _ => realChildList.addOne(item)
     }
     this.popMany(childCount)
     val tailIndex = this.stack.values.length - 1;
     var attrs:MHashMap[String,VMValue] = MHashMap.empty
     for(idx <- 0.until(attrCount)) {
         val v = this.stack.values(tailIndex - idx); 
         val k = this.stack.values(tailIndex - idx - 1);
         val strKey = k.unwrap[VMValue.VMString]().get.value;
         attrs.addOne((strKey,v))
     }
     this.popMany(attrCount * 2)
     val tagName = this.pop().unwrap[VMValue.VMString]().get.value
     val xmlValue = VMValue.VMXml(XmlNode(tagName,HashMap.from(attrs),realChildList.toVector))
     this.push(xmlValue)
   }
}

