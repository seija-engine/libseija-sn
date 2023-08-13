package sxml.vm

import scala.collection.mutable.ArrayBuffer
import scala.collection.immutable.HashMap

enum VMValue {
    case  NIL() extends VMValue
    case  VMChar(value:Char) extends VMValue
    case  VMLong(value:Long) extends VMValue
    case  VMFloat(value:Double) extends VMValue 
    case  VMString(value:String) extends VMValue
    case  VMArray(value:Vector[VMValue]) extends VMValue
    case  VMMap(value:HashMap[VMValue,VMValue]) extends VMValue
    case  VMClosure(data:ClosureData) extends VMValue

    def isFloat():Boolean = {
        this match
            case VMFloat(value) => true
            case _ => false
    }

    def castFloat():Option[Double] = {
        this match
            case VMLong(value) => Some(value.toDouble)
            case VMFloat(value) => Some(value) 
            case _ => None
    }

    def castInt():Option[Long] = {
        this match
            case VMLong(value) => Some(value)
            case VMFloat(value) => Some(value.toInt)
            case _ => None
    }

    inline def unwrap[T]():Option[T] = {
        this.match
            case v:T => Some(v)
            case _ => None
    }

    override def equals(other: Any): Boolean = {
        if(!other.isInstanceOf[VMValue]) return false
        val otherValue = other.asInstanceOf[VMValue]
        this.match
            case NIL() => otherValue.unwrap[VMValue.NIL]().isDefined
            case VMChar(value) =>  otherValue.unwrap[VMValue.VMChar]().map(v => v.value == value).getOrElse(false)
            case VMLong(value) =>  otherValue.unwrap[VMValue.VMLong]().map(v => v.value == value).getOrElse(false)
            case VMFloat(value) => otherValue.unwrap[VMValue.VMFloat]().map(v => v.value == value).getOrElse(false)
            case VMString(value) => otherValue.unwrap[VMValue.VMString]().map(v => v.value == value).getOrElse(false)
            case VMArray(thisList) =>  {
               val otherList = otherValue.unwrap[VMValue.VMArray]()
               if(otherList.isEmpty) return false
               for((av,bv) <- thisList.zip(otherList.get.value)) {
                if(!av.equals(bv)) return false
               }
               true
            }
            case _ => false
    }
}

case class ClosureData(val function:CompiledFunction,upvars:ArrayBuffer[VMValue])