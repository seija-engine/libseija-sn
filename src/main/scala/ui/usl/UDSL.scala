package ui.usl
import java.util.HashMap;
import ui.controls.Image

object UDSL {
  var types = HashMap[String,UDSLType[_]]()

  def addType[T](using typ:UDSLType[T]) = {
   

    typ.typInfo match {
      case ui.usl.TypeInfo.Class(value) => types.put(value.name, typ)
      case TypeInfo.NumberEnum(name) => types.put(name, typ)
      case ui.usl.TypeInfo.Enum(name,fields) => types.put(name, typ)
      case _ => throw new Exception("Only class type can be added to UDSL")
    }  
  }

  def getType(name:String):Option[UDSLType[_]] = Option(types.get(name))
}
