package core.reflect
import scala.collection.immutable.HashMap;
import scala.deriving.Mirror
import scala.quoted.*
import core.formString
import scala.util.Try
import scala.util.Success
import scala.quoted.ToExpr.ListToExpr
import scala.annotation.Annotation;
import scala.util.boundary,boundary.break
import scala.reflect.ClassTag

case class TypeInfo(val name:String,
                    val shortName:String,
                    val create:() => Any,
                    val base:Option[String],
                    val fieldList:List[FieldInfo],
                    val annotations:List[Annotation] = List()) {
   val fieldMap:HashMap[String,FieldInfo] = fieldList.foldLeft(HashMap[String,FieldInfo]()){(map,info) =>
      map.updated(info.Name,info)
   };

   val annotationMap:HashMap[String,Annotation] = annotations.foldLeft(HashMap[String,Annotation]()){(map,ann) =>
      map.updated(ann.getClass().getName(),ann)
   };

   def getBaseType():Option[TypeInfo] = base.flatMap(Assembly.get(_));

   def getValue(obj:Any,fieldName:String):Option[Any] = {
      val baseType = getBaseType();
      if(baseType.isDefined) {
         val baseValue = baseType.get.getValue(obj,fieldName);
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
      val baseType = getBaseType();
      if(baseType.isDefined) {
         if(baseType.get.setValue(obj,fieldName,value)) { 
            return true; 
         }
      }
      val ret = this.fieldMap.get(fieldName) match
         case None => false
         case Some(info) => {
            info.set(obj,value);
            true
         }
      ret
   }

   def getField(fieldName:String):Option[FieldInfo] = {
      val baseType = getBaseType();
      if(baseType.isDefined) {
         val baseField = baseType.get.getField(fieldName);
         if(baseField.isDefined) return baseField;
      }
      this.fieldMap.get(fieldName)
   }
 
   def getFieldTry(fieldName:String):Try[FieldInfo] = {
      this.getField(fieldName).toRight(NotFoundFieldException(this.name,fieldName)).toTry
   }

   def getAnnotation[T <: Annotation](using ev:ClassTag[T]):Option[T] = {
      val annName = ev.runtimeClass.getName();
      val value = this.annotationMap.get(annName).map(_.asInstanceOf[T])
      
      if(value.isEmpty) {
        this.getBaseType().flatMap(_.getAnnotation[T])
      } else {
         value
      }
   }

   def isInstOf(other:TypeInfo):Boolean = {
      if(this.name.equals(other.name)) {
         return true;
      }
      return this.getBaseType().map(_.isInstOf(other)).getOrElse(false)
   }
}

case class FieldInfo(val Name:String,
                     val typName:String,
                     set:(Any,Any) => Unit,
                     get:(Any) => Any);


def typeInfoOf[T](using t:ReflectType[T]):TypeInfo = t.info

trait ReflectType[T] {
   def info:TypeInfo;
}

object ReflectType {
   inline def derived[T]: ReflectType[T] = ${ derivedMacro[T] };

   def derivedMacro[T: Type](using Quotes): Expr[ReflectType[T]] = {
      import quotes.reflect.*
      val typRepr: TypeRepr = TypeRepr.of[T]
      
      val annLst = Expr.ofList(typRepr.typeSymbol.annotations.filter(t => {
         t.tpe.asType match
            case '[scala.annotation.internal.SourceFile] => false
            case _ => true
      }).map(_.asExprOf[Annotation])); 
      
      val typ = typRepr.asType;
      val typClassSym = typRepr.classSymbol.get;
      val typeSym = typRepr.typeSymbol;
      val fullName:Expr[String] = Expr(typClassSym.fullName);
      val shortName:Expr[String] = Expr(typClassSym.name);
      val init = typeSym.declarations.find(_.name=="<init>").get
      val newExpr = Apply(Select(New(TypeTree.of[T]),init),List()).asExprOf[T]
      val baseLst = typRepr.baseClasses.filter(s => !s.flags.is(Flags.Trait));
      val baseTypeName:Expr[Option[String]] = if(baseLst.length >= 2) { 
         Expr(Some(baseLst(1).fullName)) 
      } else {  Expr(None) };
      val allFields:List[Expr[FieldInfo]] = typClassSym.declaredFields.filter(filterSym => {
         !filterSym.flags.is(Flags.Private)
      }).map(fieldSym => {
         val memberType = typRepr.memberType(fieldSym);
         
         val fieldName = if(fieldSym.name.charAt(0) == '_') { fieldSym.name.tail } else { fieldSym.name };
         (memberType.asType,typRepr.asType) match {
            case ('[ft],'[t]) => {
                  val fullTypeName = Assembly.fullTypeName[ft]; 
                  '{
                     FieldInfo(
                        ${Expr(fieldName)},
                        ${fullTypeName},
                        (a,b) => {
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
         new ReflectType[T] {
            override def info: TypeInfo = TypeInfo(
               $fullName,
               $shortName,
               () => ${newExpr},
               $baseTypeName,
               ${fieldList},
               ${annLst})
         }
      }
      //report.info(ret.show)
      ret
   }
}