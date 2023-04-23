package ui.usl
import java.util.ArrayList;
import scala.collection.mutable.HashMap
import ui.controls.Image

trait UDSLType[T] {
  val typInfo: TypeInfo
  def default(): T
  def fromNumEnum(tag: Int): T = default()
  def fromClass(args: HashMap[String,Any]): Option[T]  = {
    var newClass = default();
    this.typInfo match
        case ui.usl.TypeInfo.Class(value) => {
            val setterMap: HashMap[String, (T, Any) => Unit] = HashMap()
            value.fields.forEach((field) => {
                setterMap.put(field.name, field.setter.asInstanceOf[(T, Any) => Unit])
            })
            for((key, value) <- args) {
                setterMap.get(key) match {
                    case Some(setter) => setter(newClass, value)
                    case None => throw new Exception("No setter for field: " + key)
                }
            }
        }
        case _ => return None
    Some(newClass)
  }
  def fromEnum(tag:Int,args:List[Any]):T = default()
}

enum TypeInfo {
  case Int
  case Float
  case String
  case Bool
  case Class(value: ClassType[_])
  case NumberEnum(val name: String)
  case Enum(fields: List[EnumItem])
}

case class EnumItem(val name: String, val value: Option[TypeInfo]);

case class ClassType[T](val name: String) {
  case class FiledInfo[F](
      val name: String,
      val typ: TypeInfo,
      val setter: (T, F) => Unit
  )

  val fields: ArrayList[FiledInfo[_]] = ArrayList()

  def field[F](
      name: String,
      typ: TypeInfo,
      setter: (T, F) => Unit
  ): ClassType[T] = {
    this.fields.add(new FiledInfo(name, typ, setter))
    this
  }
}
