package sxml.parser
import scala.collection.immutable

enum CExpr {
    case Nil
    case SList(lst:List[CExpr])
    case SComment(value:String)
    case SVector(lst:List[CExpr])
    case SXMLExpr(value:XmlExpr)
    case SLit(value:LitValue)
}

enum LitValue {
    case LLong(value:Long)
    case LFloat(value:Double)
    case LString(value:String)
    case LBool(value:Boolean)
    case LChar(value:Char)
}

case class XmlExpr(tag:String)