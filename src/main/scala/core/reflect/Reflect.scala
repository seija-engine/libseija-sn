package core.reflect
import scala.collection.immutable.HashMap;


case class TypeInfo(val name:String,
                    val create:() => Any,
                    val base:Option[TypeInfo],
                    val fieldList:List[FieldInfo]) {
   val fieldMap:HashMap[String,FieldInfo] = fieldList.foldLeft(HashMap[String,FieldInfo]()){(map,info) =>
      map.updated(info.Name,info)
   };

   def getValue(obj:Any,fieldName:String):Option[Any] = {
      if(base.isDefined) {
         val baseValue = base.get.getValue(obj,fieldName);
         if(baseValue.isDefined) return baseValue;
      }
      this.fieldMap.get(fieldName) match
         case None => None
         case Some(value) => Some(value.get(obj))
   }

   def getValue_?(obj:Any,fieldName:String):Any = {
      this.getValue(obj,fieldName).getOrElse(throw new NotFoundFieldException(this.name,fieldName))
   }

   def setValue(obj:Any,fieldName:String,value:Any):Boolean = {
      if(base.isDefined) {
         if(base.get.setValue(obj,fieldName,value)) { return true; }
      }
      this.fieldMap.get(fieldName) match
         case None => false
         case Some(info) => {
            info.set(obj,value);
            true
         }
   }

   def getField(fieldName:String):Option[FieldInfo] = {
      if(base.isDefined) {
         val baseField = base.get.getField(fieldName);
         if(baseField.isDefined) return baseField;
      }
      this.fieldMap.get(fieldName)
   }

   def getField_?(fieldName:String):FieldInfo = {
      this.getField(fieldName).getOrElse(throw new NotFoundFieldException(this.name,fieldName))
   }
}

case class FieldInfo(val Name:String,set:(Any,Any) => Unit,get:(Any) => Any);

trait ReflectType[T] {
   def info:TypeInfo;
}

def typeInfoOf[T](using t:ReflectType[T]):TypeInfo = t.info