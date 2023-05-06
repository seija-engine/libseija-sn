package core.reflect
import scala.collection.immutable.HashMap;


case class TypeInfo(val name:String,
                    val create:() => Any,
                    val base:Option[TypeInfo],
                    val fieldList:List[FieldInfo]) {
   val fieldMap:HashMap[String,FieldInfo] = fieldList.foldLeft(HashMap[String,FieldInfo]()){(map,info) =>
      map.updated(info.Name,info)
   };

   def GetValue(obj:Any,fieldName:String):Option[Any] = {
      if(base.isDefined) {
         val baseValue = base.get.GetValue(obj,fieldName);
         if(baseValue.isDefined) return baseValue;
      }
      this.fieldMap.get(fieldName) match
         case None => None
         case Some(value) => Some(value.get(obj))
   }

   def GetField(fieldName:String):Option[FieldInfo] = {
      if(base.isDefined) {
         val baseField = base.get.GetField(fieldName);
         if(baseField.isDefined) return baseField;
      }
      this.fieldMap.get(fieldName)
   }
}

case class FieldInfo(val Name:String,set:(Any,Any) => Unit,get:(Any) => Any);

trait ReflectType[T] {
   def info:TypeInfo;
}

def typeInfoOf[T](using t:ReflectType[T]):TypeInfo = t.info