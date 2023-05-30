package core.reflect
import java.util.HashMap
import scala.quoted.*
import scala.collection.mutable.ListBuffer
import scala.util.Try

case class NotFoundTypeInfoException(name: String)  extends Exception(s"not found type info: ${name}")
case class NotFoundFieldException(className: String, name: String) extends Exception(s"not found field: ${className}.${name}")
case class NotFoundReflectException(name: String) extends Exception(s"not found reflect: ${name}")

object Assembly {
  private var typeMap: HashMap[String, TypeInfo] = HashMap()
  private var typeShortMap: HashMap[String, TypeInfo] = HashMap()

  inline def add[T]()(using t: ReflectType[T]) = { this.addTypeInfo(t.info); }

  def addTypeInfo(info:TypeInfo) = {
    println(s"add type info: ${info.name}")
    this.typeShortMap.put(info.shortName, info);
    this.typeMap.put(info.name, info);
  }

  def has(name:String):Boolean = { this.typeMap.containsKey(name); }

  def get(name: String): Option[TypeInfo] = Option(this.typeMap.get(name))

  def getTry(name: String): Try[TypeInfo]  =  {
    this.get(name).toRight(NotFoundReflectException(name)).toTry
  }

  def getTypeInfo(obj: Any): Option[TypeInfo] = Option(
    this.typeMap.get(obj.getClass().getName())
  )

  def getTypeInfoOrThrow(obj: Any): TypeInfo = this
    .getTypeInfo(obj)
    .getOrElse(throw new NotFoundTypeInfoException(obj.getClass().getName()))

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

  inline def scanPackage(inline sym:Any) = ${ scanPackageImpl('sym) }

  protected def scanPackageImpl(symExpr:Expr[Any])(using Quotes):Expr[Unit] = {
    import quotes.reflect.*;
    val parentSym = symExpr.asTerm.tpe.classSymbol.get.owner;
    val derivedSymList = parentSym.declaredTypes.flatten(_.declarations)
                                   .filter(s => s.name.startsWith("derived$") && !s.isType);
    var allStrings = "";
    val allStmtList:ListBuffer[Statement] = ListBuffer();
    for(deriveSym <- derivedSymList) {
      val infoSym = deriveSym.declaredMethod("info")(0); 
      val applyTerm = Select(Ident(deriveSym.termRef),infoSym).asExprOf[TypeInfo];
      allStrings += applyTerm.show + "\n";
      allStmtList.addOne('{Assembly.addTypeInfo(${applyTerm})}.asTerm);
    }
    val block = Block(allStmtList.toList,Literal(UnitConstant()));
    //report.info(block.show); 
    block.asExprOf[Unit]
  }
}


