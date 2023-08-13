package sxml.vm
import scala.collection.mutable.ArrayBuffer
import sxml.vm.VMValue
import scala.util.Try
import scala.math.Numeric.DoubleIsFractional
import scala.math.Ordering.LongOrdering
import scala.collection.immutable.HashMap

class VMStack {
   val values:ArrayBuffer[VMValue] = ArrayBuffer.empty
   val frames:ArrayBuffer[VMCallStack] = ArrayBuffer.empty

   def apply(index:Int):VMValue = this.values(index)

   def enterCallStack(offset:Int,state:ClosureState):VMCallStack = {
      val callStack = VMCallStack(offset,this,state)
      this.frames.addOne(callStack)
      callStack
   }

   def exitCallStack():Unit = {
      this.frames.remove(this.frames.length - 1)
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

   def execute_():Try[Unit] = Try {
      println("execute_")
      this.curIndex = this.state.instructionIndex
      var isRun = true
      while(isRun) {
         val instr = this.instruction()
         val index = this.curIndex
         this.step()
         println(s"do:-${instr}-")
         instr match
            case Instruction.PushNil => { this.stack.values.addOne(VMValue.NIL()) }
            case Instruction.PushInt(value) => { this.stack.values.addOne(VMValue.VMLong(value)) }
            case Instruction.PushChar(value) => {  this.stack.values.addOne(VMValue.VMChar(value)) }
            case Instruction.PushFloat(value) => { this.stack.values.addOne(VMValue.VMFloat(value)) }
            case Instruction.PushString(value) => {
               val str = this.state.closure.function.strings(value)
               this.stack.values.addOne(VMValue.VMString(str))
            }
            case Instruction.Push(idx) => {
               val value = this.stack.values(idx)
               this.stack.values.addOne(value)
            }
            case Instruction.ConstructArray(count) => {
               val takeList = this.stack.values.slice(this.stack.values.length - count,this.stack.values.length).toVector
               this.stack.values.remove(this.stack.values.length - count,count)
               this.stack.values.addOne(VMValue.VMArray(takeList))
            }
            case Instruction.ConstructMap(count) => {
               val rmCount = count * 2
               val takeList = this.stack.values.slice(this.stack.values.length - rmCount,this.stack.values.length).toArray
               this.stack.values.remove(this.stack.values.length - rmCount,rmCount)
               val pushMap:HashMap[VMValue,VMValue] = HashMap.from(0.until(takeList.length,2)
                                                             .map(idx => (takeList(idx),takeList(idx+1))))
               this.stack.values.addOne(VMValue.VMMap(pushMap))
               
            }
            case Instruction.NewClosure(cidx, upvars) => {
               val func = this.state.closure.function.innerFunctions(cidx)
               val lst:ArrayBuffer[VMValue] = ArrayBuffer.from(0.until(upvars).map(_ =>VMValue.NIL()))
               this.stack.values.addOne(VMValue.VMClosure(ClosureData(func, lst))) 
            }
            case Instruction.CloseClosure(count) => {
               val i = this.stack.values.length - count - 1;
               val closure = this.stack.values(i).unwrap[VMValue.VMClosure]().get.data
               val start = this.stack.values.length - closure.upvars.length;
               for(idx <- 0.until(closure.upvars.length)) {
                 closure.upvars.update(idx,this.stack.values(start + idx))
               }
               
               val pop = closure.upvars.length + 1;
               this.stack.values.remove(this.stack.values.length - pop,pop)
            }
            case Instruction.PushUpVar(idx) => {
               val upVar = this.state.closure.upvars(idx)
               this.stack.values.addOne(upVar)
            }
            case Instruction.Call(value) => {

            }
            
            case Instruction.ConstructXML(attrCount, childCount) => {

            }
            case Instruction.CJump(index) => {

            }
            case Instruction.Jump(index) => {

            }
            case Instruction.Slide(count) => {

            }
            case Instruction.Pop(count) => {

            }
            case Instruction.ReplaceTo(idx) => {

            }
            case Instruction.UnWrap => {

            }
            case Instruction.PushKW(value) => {
               
            }
            case Instruction.EQ => {
               val rhs = this.stack.values.remove(this.stack.values.length - 1)
               val lhs = this.stack.values.remove(this.stack.values.length - 1)
               this.pushBoolean(rhs.equals(lhs))
               
            }
            case Instruction.Add | Instruction.Subtract | Instruction.Multiply |
                 Instruction.Divide | Instruction.LT | Instruction.GT => {
                  this.binNumberOp(instr)
            }
            case Instruction.Return => {
               isRun = false
            }
         
         println(this.stack.values.mkString("\r\n"))
         println(s"====END:${index}====")
      }
   }

  
   
   def pushBoolean(b:Boolean):Unit = {
      this.stack.values.addOne(VMValue.VMChar( if(b) '1' else '0'))
   }
   
   inline def binNumberOp(instr:Instruction):Unit = {
      val rhs = this.stack.values.remove(this.stack.values.length - 1)
      val lhs = this.stack.values.remove(this.stack.values.length - 1)
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
}

