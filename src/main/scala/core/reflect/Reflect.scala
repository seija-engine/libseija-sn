package core.reflect
import scala.collection.immutable.HashMap;
import scala.deriving.Mirror
import scala.quoted.*
import core.formString
import scala.util.Try
import scala.util.Success

case class TypeInfo(val name:String,
                    val shortName:String,
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

   def setStringValue(obj:Any,fieldName:String,str:String):Try[Boolean] = Try {
      val convValue = this.fieldFromString(fieldName,str).get;
      this.setValue(obj,fieldName,convValue)
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

   def fieldFromString(fieldName:String,str:String):Try[Any] = {
      val getFied =  this.getField_?(fieldName);
      getFied.fromString match {
         case None => throw new Exception(s"not found fromString: ${this.name}.${fieldName}")
         case Some(conv) => Try(conv(str))
      }
   }

  
}

case class FieldInfo(val Name:String,
                     val typName:String,
                     set:(Any,Any) => Unit,
                     get:(Any) => Any,
                     val fromString:Option[(String) => Any] = None);


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
      val shortName:Expr[String] = Expr(typClassSym.name);
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
                  val fullTypeName = Assembly.fullTypeName[ft];
                  val exprGetString = getFormStringExpr[ft]();
                  val exprFromString:Expr[Option[(String) => Any]] = exprGetString match {
                     case None => '{None}
                     case Some(value) => '{Some($value)}
                  }
                  
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
                         (obj:Any) => ${Select('{obj.asInstanceOf[t]}.asTerm,fieldSym).asExpr},
                         ${exprFromString}
                     )
                  }
            }
         }
         
      });
      val fieldList:Expr[List[FieldInfo]] = Expr.ofList(allFields);
      val ret = '{
         val baseTypeInfo = Assembly.get($baseTypeName,false);
         new ReflectType[T] {
            override def info: TypeInfo = TypeInfo($fullName,$shortName,() => ${newExpr},baseTypeInfo,${fieldList})
         }
      }
      ret
   }

   def getFormStringExpr[T:Type]()(using Quotes):Option[Expr[(String) => Any]] = {
      import quotes.reflect.*
      val typRepr: TypeRepr = TypeRepr.of[T]
      typRepr.asType match {
         case '[Boolean] => Some('{str2Bool })
         case '[Byte] => Some('{str2Byte })
         case '[Int] =>  Some('{str2Int})
         case '[Float] => Some('{str2Float })
         case '[String] => Some('{ str2str})
         case '[ui.Template] => None
         case '[Option[tt]] => {
            val expr = getFormStringExpr[tt](); 
            expr match {
               case None => None
               case Some(value) => Some('{ s => Some(${value}(s)) })
            }
         }
         case '[t] => {
            var allString = "";
            val fromStringSymLst = typRepr.typeSymbol.companionModule.declarations.filter(_.name.startsWith("given_IFromString"));
            if(fromStringSymLst.isEmpty) return None;
            val fromStringSym = fromStringSymLst.head;
            val from = fromStringSym.declaredMethod("from");
            val select =  Select(Ident(fromStringSym.termRef),from(0));
            val lambda = select.etaExpand(fromStringSym.owner).asExprOf[String => Option[t]];
            val typName = Expr(typRepr.typeSymbol.fullName);
            val ret = '{ (s:String) => ${lambda}(s).getOrElse(throw new Exception( "parse '" + s + "' to " + ${typName} + " error ")) }
            Some(ret)
         }
      }
   }

   protected def str2Bool(str:String):Boolean = str.toBooleanOption.getOrElse(false)
   protected def str2Int(str:String):Int = str.toIntOption.getOrElse(0)
   protected def str2Byte(str:String):Byte = str.toByteOption.getOrElse(0)
   protected def str2Float(str:String):Float = str.toFloatOption.getOrElse(0.0f)
   protected def str2str(str:String):String = str
}