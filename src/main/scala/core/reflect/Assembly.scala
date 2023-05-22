package core.reflect
import java.util.HashMap
import scala.quoted.*

object Assembly {
  private var typeMap: HashMap[String, TypeInfo] = HashMap()
  private var typeShortMap: HashMap[String, TypeInfo] = HashMap()

  inline def add[T]()(using t: ReflectType[T]) = {
    val typInfo = t.info;
    this.typeShortMap.put(typInfo.shortName, typInfo);
    this.typeMap.put(typInfo.name, typInfo);
  }

  def get(name: String, isShort: Boolean = false): Option[TypeInfo] = Option(
    if (isShort) { this.typeShortMap.get(name) }
    else { this.typeMap.get(name) }
  )

  def getTypeInfo(obj: Any): Option[TypeInfo] = Option(
    this.typeMap.get(obj.getClass().getName())
  )

  def getTypeInfo_?(obj: Any): TypeInfo = this
    .getTypeInfo(obj)
    .getOrElse(throw NotFoundTypeInfoException(obj.getClass().getName()))

  def createInstance(name: String, isShort: Boolean = false): Option[Any] = {
    val typInfo = if (isShort) this.typeShortMap.get(name) else this.typeMap.get(name);
    if (typInfo == null) return None;
    Some(typInfo.create())
  }


  inline def nameOf[T] = ${fullTypeName[T]}


  def fullTypeName[T:Type](using Quotes):Expr[String] = {
    import quotes.reflect.*;
    def getReprName(repr:TypeRepr):String = {
      val childNameLst  = repr.typeArgs.map(getReprName);
      if(childNameLst.length > 0) {
        s"${repr.dealias.typeSymbol.fullName}[${childNameLst.mkString(",")}]"
      } else {
        repr.dealias.typeSymbol.fullName
      }
    }
    Expr(getReprName(TypeRepr.of[T]))
  }
}

case class NotFoundTypeInfoException(name: String)
    extends Exception(s"not found type info: ${name}")
case class NotFoundFieldException(className: String, name: String)
    extends Exception(s"not found field: ${className}.${name}")
