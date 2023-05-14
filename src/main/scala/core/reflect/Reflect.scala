package core.reflect
import scala.collection.immutable.HashMap;
import scala.deriving.Mirror
import scala.quoted.*
import scala.annotation.internal.Body

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



def typeInfoOf[T](using t:ReflectType[T]):TypeInfo = t.info

trait ReflectType[T] {
   def info:TypeInfo;
}

object ReflectType {
   inline def derived[T]: ReflectType[T] = ${ derivedMacro[T] };

   def derivedMacro[T: Type](using Quotes): Expr[ReflectType[T]] = {
      import quotes.reflect.*
      val typRepr: TypeRepr = TypeRepr.of[T]
      val typ = typRepr.asType;
      val typClassSym = typRepr.classSymbol.get;
      val typeSym = typRepr.typeSymbol;
      val fullName:Expr[String] = Expr(typClassSym.fullName);
      val init = typeSym.declarations.find(_.name=="<init>").get
      val newExpr = Apply(Select(New(TypeTree.of[T]),init),List()).asExprOf[T]
      val baseTypeName = if(typRepr.baseClasses.length >= 2) { 
         Expr(typRepr.baseClasses(1).fullName) 
      } else {  null };
      val allFields:List[Expr[FieldInfo]] = typClassSym.declaredFields.map(fieldSym => {
         val memberType = typRepr.memberType(fieldSym);
         val fieldName = if(fieldSym.name.charAt(0) == '_') { fieldSym.name.tail } else { fieldSym.name };
         (memberType.asType,typRepr.asType) match {
            case ('[ft],'[t]) => {
                  '{
                     FieldInfo(
                        ${Expr(fieldName)}
                        ,(a,b) => {
                            ${
                               val selectField = Select('{a.asInstanceOf[t]}.asTerm,fieldSym);
                               Assign(selectField,'{b.asInstanceOf[ft]}.asTerm).asExpr
                            }
                         },
                         (obj:Any) => ${Select('{obj.asInstanceOf[t]}.asTerm,fieldSym).asExpr}
                     )
                  }
            }
         }
         
      });
      val fieldList:Expr[List[FieldInfo]] = Expr.ofList(allFields);
      val ret = '{
         val baseTypeInfo = Assembly.get($baseTypeName);
         new ReflectType[T] {
            override def info: TypeInfo = TypeInfo($fullName,() => ${newExpr},baseTypeInfo,${fieldList})
         }
      }
      ret
   }

}