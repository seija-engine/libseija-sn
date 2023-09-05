package sxml.vm
import sxml.parser.LitValue
import sxml.parser.TextSpan

enum VMExpr {
    case VMNil
    case VMLit(value:LitValue)
    case VMArray(value:Vector[TextSpan[VMExpr]])
    case VMMap(value:Vector[TextSpan[VMExpr]])
    case VMSymbol(value:Symbol)
    case VMKeyword(value:String,isLocal:Boolean)
    case VMCall(fn:TextSpan[VMExpr],args:Vector[TextSpan[VMExpr]])
    case VMMatch(value:TextSpan[VMExpr],alts:Vector[Alternative])
    case VMDef(name:Symbol,expr:TextSpan[VMExpr])
    case VMFunc(args:Vector[Symbol],bodyLst:Vector[TextSpan[VMExpr]])
    case VMLet(lets:Vector[TextSpan[VMExpr]],bodyLst:Vector[TextSpan[VMExpr]],isLoop:Boolean)
    case VMRecur(lst:Vector[TextSpan[VMExpr]])
    case VMXml(tag:String,attrs:Vector[(String,TextSpan[VMExpr])],child:Vector[TextSpan[VMExpr]])
    case VMUnWrap(value:TextSpan[VMExpr])
    case VMExport
    case VMImport
}

inline def vmExprCastTo[T <: VMExpr](vmExpr:VMExpr):Option[T] = if(vmExpr.isInstanceOf[T]) Some(vmExpr.asInstanceOf[T]) else None

case class Symbol(ns:Option[String],name:String) {
  override def toString(): String = ns match {
    case None => name
    case Some(value) => s"${value}/${name}"
  }
}

enum AltPattern {
   case Literal(value:LitValue)
   case Array(lst:Vector[TextSpan[VMExpr]])
   case Ident(symbol:Symbol)
}

case class Alternative(
  val pattern:AltPattern,
  val expr:TextSpan[VMExpr]
)