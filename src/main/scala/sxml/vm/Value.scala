sealed trait SValue

case class SLong(value:Long) extends SValue
case class SByte(value:Byte) extends SValue
case class SString(value:String) extends SValue
case class SArray(value:List[SValue]) extends SValue