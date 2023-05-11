package core.reflect
import java.util.HashMap

object Assembly {
    private var typeMap:HashMap[String,TypeInfo] = HashMap()

    inline def add[T]()(using t:ReflectType[T]) = {
        val typInfo = t.info;
        this.typeMap.put(typInfo.name,typInfo);
    }

    
    def getTypeInfo(obj:Any):Option[TypeInfo] = Option(this.typeMap.get(obj.getClass().getName()))

    def getTypeInfo_?(obj:Any):TypeInfo = this.getTypeInfo(obj).getOrElse(throw NotFoundTypeInfoException(obj.getClass().getName()))

    def createInstance(name:String):Option[Any] = {
       val typInfo = this.typeMap.get(name);
       if(typInfo == null) return None;
       Some(typInfo.create())
    }
}

case class NotFoundTypeInfoException(name:String) extends Exception(s"not found type info: ${name}")
case class NotFoundFieldException(className:String,name:String) extends Exception(s"not found field: ${className}.${name}")