package sxml.parser
import scala.collection.mutable.ArrayBuffer

enum CExpr {
    case Nil
    case SList(lst:ArrayBuffer[CExpr])
    case SVector(lst:ArrayBuffer[CExpr])
    case SMap(lst:ArrayBuffer[CExpr])
    case SComment(value:String)
    case SSymbol(ns:Option[String],value:String)
    case SXMLElement(tag:String,attrList:ArrayBuffer[(String,CExpr)],child:ArrayBuffer[CExpr])
    case SKeyworld(value:String,isLocal:Boolean)
    case SXMLExpr(value:XmlExpr)
    case SUnWrap(value:CExpr)
    case SDispatch(value:CExpr)
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