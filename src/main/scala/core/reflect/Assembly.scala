package core.reflect
import java.util.HashMap

object Assembly {
    private var typeMap:HashMap[String,TypeInfo] = HashMap()

    inline def add[T]()(using t:ReflectType[T]) = {
        val typInfo = t.info;
        this.typeMap.put(typInfo.name,typInfo);
    }

    def getTypeInfo(obj:Any):Option[TypeInfo] = Option(this.typeMap.get(obj.getClass().getName()))
}