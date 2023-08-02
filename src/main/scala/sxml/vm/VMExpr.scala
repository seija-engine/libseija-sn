package sxml.vm
import sxml.parser.LitValue
import sxml.parser.TextSpan

enum VMExpr {
    case VMNil
    case VMLit(value:LitValue)
    case VMArray(value:Vector[TextSpan[VMExpr]])
    case VMSymbol(value:Symbol)
    case VMCall(fn:TextSpan[VMExpr],args:Vector[TextSpan[VMExpr]])
    case VMMatch(value:TextSpan[VMExpr],alts:Vector[Alternative])
    case VMDef(name:Symbol,expr:TextSpan[VMExpr])
    case VMFunc(args:Vector[Symbol],bodyLst:Vector[TextSpan[VMExpr]])
}


case class Symbol(ns:Option[String],name:String)

enum AltPattern {
   case Literal(value:LitValue)
}

case class Alternative(
  val pattern:AltPattern,
  val expr:TextSpan[VMExpr]
)