package com.seija.sxml.parser
import scala.collection.mutable.ArrayBuffer

enum CExpr {
    case Nil
    case SList(lst:ArrayBuffer[TextSpan[CExpr]])
    case SVector(lst:ArrayBuffer[TextSpan[CExpr]])
    case SMap(lst:ArrayBuffer[TextSpan[CExpr]])
    case SSymbol(ns:Option[String],value:String)
    case SXMLElement(tag:String,attrList:ArrayBuffer[(String,TextSpan[CExpr])],child:ArrayBuffer[TextSpan[CExpr]])
    case SKeyworld(value:String,isLocal:Boolean)
    case SUnWrap(value:TextSpan[CExpr])
    case SDispatch(value:TextSpan[CExpr])
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