package core.reflect
import scala.quoted.*
import scala.util.Try
import scala.reflect.{ClassTag,classTag}
import scala.quoted.Expr
import scala.annotation.internal.Body
import scala.collection.mutable.{ListBuffer,HashMap}
import core.reflect.Assembly.nameOf

case class TypeCastException(from:String,to:String) extends Exception(s"${from} to ${to} err")

trait Into[A,B] {
    def into(fromValue: A): B;
    def tryInto(fromValue: A): Try[B] =  Try(into(fromValue))
}

def convert[A,B](fromValue:A)(using into:Into[A,B]):Try[B] = into.tryInto(fromValue)

def tryInto[A,B](fromValue:A)(using into:Into[A,B]):Try[B] = into.tryInto(fromValue)

object DynTypeConv {
    private val convMap = HashMap[(String,String),Into[_,_]]();
    
    def init() = {
        register[String,Int]
        register[String,Float]
        register[String,Double]
        register[String,Boolean]
        register[String,String]
    }
    
    inline def register[A,B](using into: Into[A,B]): Unit = { 
        val aName = Assembly.nameOf[A];
        val bName = Assembly.nameOf[B];
        val key = (aName,bName);
        if(!this.convMap.contains(key)) {
            this.convMap.put(key,into);
        }
    }

    def registerString(fromType:String,toType:String,into:Into[_,_]) = {
        println(s"register ${fromType} to ${toType}")
        val key = (fromType,toType);
        if(!this.convMap.contains(key)) {
            this.convMap.put(key,into);
        }
    }

    

    def convertStrType(fromType:String,toType:String,fromValue:Any):Option[Try[Any]] = {
        val key = (fromType,toType);
        if(this.convMap.contains(key)) {
            val conv = this.convMap(key);
            val toValue = conv.tryInto.asInstanceOf[Any=>Try[Any]](fromValue);
            Some(toValue)
        } else {
            None
        }
    }

    def strConvertTo(toType:String,fromValue:String):Option[Try[Any]] = {
        this.convertStrType(nameOf[String],toType,fromValue)
    }

    def convert(fromType:Class[?],toType:Class[?],fromValue:Any):Option[Try[Any]] = {
        this.convertStrType(fromType.getName(),toType.getName(),fromValue)
    }

    inline def scanPackage(inline sym:Any):Unit = ${ scanPackageImpl('sym) }

    protected def scanPackageImpl(symExpr:Expr[Any])(using Quotes):Expr[Unit] = {
      import quotes.reflect.*
      val parentSym = symExpr.asTerm.tpe.classSymbol.get.owner;
      val givenIntoSymList = parentSym.declaredTypes.flatten(_.declarations).filter(_.name.startsWith("given_Into"));
      val allTypeList:ListBuffer[Statement] = ListBuffer();
      var allStrings ="";
      for(declSym <- givenIntoSymList) {
         if(!declSym.isType) {
            val intoMethod = declSym.declaredMethod("into");
            val signature = intoMethod(0).signature;
            val fromType = Expr(signature.paramSigs(0).toString());
            
            val toType =  intoMethod(0).tree match
              case DefDef(v1,v2,v3,v4) => v3.show
              case _ => signature.resultSig
            //allStrings += toType + "\n";
            val ident = Ident(declSym.termRef);
            val identExpr = ident.asExprOf[Into[_,_]];
            val newExpr = '{ DynTypeConv.registerString($fromType,${Expr(toType)},$identExpr) }
            allTypeList.addOne(newExpr.asTerm);
         }
      }
      report.info(allStrings);
      val block = Block(allTypeList.toList,Literal(UnitConstant()));
      block.asExprOf[Unit]
    }
}

given Into[String, Int] with {
  override def into(a: String): Int = a.toInt
}

given Into[String, Float] with {
  override def into(a: String): Float = a.toFloat
}

given Into[String, Double] with {
  override def into(a: String): Double = a.toDouble
}

given Into[String, Boolean] with {
  override def into(a: String): Boolean = a.toBoolean
}

given Into[String, String] with {
  override def into(a: String): String = a
}

given [T](using t:Into[T,String]):Into[Option[T],String] with {
  override def into(value: Option[T]): String = value match
    case None => ""
    case Some(value) => t.into(value)
}