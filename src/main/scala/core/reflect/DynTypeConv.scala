package core.reflect
import scala.quoted.*
import scala.util.Try
import scala.reflect.{ClassTag,classTag}
import scala.quoted.Expr
import scala.annotation.internal.Body
import scala.collection.mutable.ListBuffer

case class TypeCastException(msg:String) extends Exception(msg)

trait Into[A,B] {
    def into(fromValue: A): B;
    def tryInto(fromValue: A): Try[B] =  Try(into(fromValue))
}

def convert[A,B](fromValue:A)(using into:Into[A,B]):Try[B] = into.tryInto(fromValue)

object DynTypeConv {
    private val convMap = collection.mutable.Map[(String,String),Into[_,_]]();
    def init() = this.addInnerConv();
    inline def register[A,B](using into: Into[A,B]): Unit = { 
        val aName = Assembly.nameOf[A];
        val bName = Assembly.nameOf[B];
        val key = (aName,bName);
        println(s"registering $key")
        if(!this.convMap.contains(key)) {
            this.convMap.put(key,into);
        }
    }

    def registerInto(fromType:String,toType:String,into:Into[_,_]) = {
        
        val key = (fromType,toType);
        println(s"registering $key")
        if(!this.convMap.contains(key)) {
            this.convMap.put(key,into);
        }
    }

    

    def strConvert(fromType:String,toType:String,fromValue:Any):Option[Try[Any]] = {
        val key = (fromType,toType);
        if(this.convMap.contains(key)) {
            val conv = this.convMap(key);
            val toValue = conv.tryInto.asInstanceOf[Any=>Try[Any]](fromValue);
            Some(toValue)
        } else {
            None
        }
    }

    def convert(fromType:Class[?],toType:Class[?],fromValue:Any):Option[Try[Any]] = {
        this.strConvert(fromType.getName(),toType.getName(),fromValue)
    }


   
    private def addInnerConv() = {
        register[String,Int]
        register[String,Float]
        register[String,Double]
        register[String,Boolean]
        register[String,String]
    }

    inline def scanPackage(inline sym:Any):Unit = ${ scanPackageImpl('sym) }

    protected def scanPackageImpl(symExpr:Expr[Any])(using Quotes):Expr[Unit] = {
      import quotes.reflect.*
      val parentSym = symExpr.asTerm.tpe.classSymbol.get.owner;
      val givenIntoSymList = parentSym.declaredTypes.flatten(_.declarations).filter(_.name.startsWith("given_Into"));
      val allTypeList:ListBuffer[Statement] = ListBuffer();
      for(declSym <- givenIntoSymList) {
         if(!declSym.isType) {
            val ident = Ident(declSym.termRef);
            val identExpr = ident.asExprOf[Into[_,_]];
            
            val newExpr = '{ DynTypeConv.registerInto("","",$identExpr) }
            
            allTypeList.addOne(newExpr.asTerm);
         }
      }
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

