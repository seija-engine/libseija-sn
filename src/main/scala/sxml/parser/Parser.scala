package sxml.parser
import scala.io.Source
import scala.util.{Failure, Success, Try}
import scala.collection.mutable.ArrayBuffer
import java.lang.Long as JLong
import java.lang.Integer as JInteger
import java.lang.Double as JDouble
import scala.util.boundary,boundary.break
import scala.collection.mutable

case class TextSpan[T](pos:SpanPos,value:T)
object TextSpan {
  def apply[T](start:LexPos,end:LexPos,value:T):TextSpan[T] = TextSpan(SpanPos(start,end),value)
}
case class SpanPos(start:LexPos,end:LexPos)

case class ParseModule(val name:String,exprList:ArrayBuffer[TextSpan[CExpr]])

class Parser(sourceName:String,lexString: LexString) {

  def parseModule():Try[ParseModule] = Try {
     val exprList = this.parseALL().get
     ParseModule(sourceName,exprList)
  }

  def parseALL():Try[ArrayBuffer[TextSpan[CExpr]]] = Try {
    var curExpr:TextSpan[CExpr] = this.parse().get
    val exprList:ArrayBuffer[TextSpan[CExpr]] = ArrayBuffer.empty
    boundary(while(true) {
      exprList += curExpr
      this.skipWhitespace()
      if(this.lexString.lookahead(1).isEmpty) {
        break()
      }
      curExpr = this.parse().get
    })
    exprList
  }

  private def parse():Try[TextSpan[CExpr]] = {
    this.skipWhitespace()
    val curChr = this.lexString.next()
    if(curChr.isDefined) {
      val ret = curChr.get match
        case '"' => this.parseString()
        case '\\' => this.parseChar()
        case '(' => this.parseList()
        case '@' => {
          val start = this.lexString.pos
          val expr = this.parse()
          if(expr.isFailure) return expr
          val end = this.lexString.pos
          Success(TextSpan(start,end,CExpr.SUnWrap(expr.get.value) ))
        }
        case '#' => {
          val start = this.lexString.pos
          val expr = this.parse()
          if(expr.isFailure) return expr
          val end = this.lexString.pos
          Success(TextSpan(start,end,CExpr.SDispatch(expr.get.value) ))
        }
        case '[' => this.parseVector()
        case '{' => this.parseMap()
        case '<' => {
          val nextChar = this.lexString.lookahead(1)
          if(nextChar.isDefined && CharUtils.isXMLSymStart(nextChar.get)) {
            this.parseXML()
          } else {
            this.parseSymbol('<')
          }
        }
        case '-' => {
          val nchr = this.lexString.lookahead(1);
          if(nchr.isDefined && nchr.get.isDigit) {
            this.lexString.next()
            this.parseNumber(nchr.get,true)
          } else {
            val end = this.lexString.pos
            Success(TextSpan(end,end,CExpr.SSymbol(None,"-")))
          }
        }
        case  chr  => {
          if(chr.isDigit) { return this.parseNumber(chr,false) }
          val sym = this.parseSymbol(chr)
          sym
        }
      return ret
    }
    Failure(ErrorEOF(this.lexString.pos))
  }

  def skipWhitespace():Unit = {
    boundary(while(true)  {
      println("loop this f1")
      val nextChar = this.lexString.lookahead(1)
       println(s"loop this f1:${nextChar}")
      if(nextChar.isEmpty) {
        break()
      }
      this.skipWhitespace()
      println(s"loop this f2:${this.lexString.lookahead(1)}")
      if(this.lexString.lookahead(1) == Some(';')) {
        this.lexString.next()
        this.skipComment()
      } else {
        break()
      }
      println("loop this f")
    })
  }

  private def parseString():Try[TextSpan[CExpr]] = {
    val start:LexPos = this.lexString.pos
    val normalF = (chr:Char) => chr != '"' && chr != '\\'
    var normalString = this.lexString.takeWhile(normalF)
    val outBuilder = StringBuilder()
    while(normalString.isDefined) {
      outBuilder.append(normalString.get)
      val nextChr = this.lexString.lookahead(1)
      nextChr match
        case Some('\"') => { this.lexString.next(); normalString = None }
        case Some('t') =>  { this.lexString.next(); outBuilder.append('\t') }
        case Some('r') =>  { this.lexString.next(); outBuilder.append('\r') }
        case Some('n') =>  { this.lexString.next(); outBuilder.append('\n') }
        case Some('\\') => { this.lexString.next(); outBuilder.append('\\') }
        case Some(_) => { return Failure(ErrCharInGap(this.lexString.pos)) }
        case None => return Failure(ErrorEOF(this.lexString.pos))
      if(normalString.isDefined) normalString = this.lexString.takeWhile(normalF)
    }
    val end = this.lexString.pos
    val expr = CExpr.SLit(LitValue.LString(outBuilder.toString()))
    Success(TextSpan(start, end, expr))
  }

  private def skipComment():Unit = { this.lexString.dropWhile(chr => chr != '\r' && chr != '\n') }

  private def parseChar():Try[TextSpan[CExpr]] = {
    val start = this.lexString.pos
    val token = this.lexString.takeWhile(chr => chr == ',' || (CharUtils.isSymChar(chr) && !chr.isWhitespace ) )
    val end = this.lexString.pos
    token match
      case Some(value) => {
        if(value.length == 1) {
          val expr = CExpr.SLit(LitValue.LChar(value(0)))
          return Success(TextSpan(start,end,expr))
        }
        val strValue = value.toString
        strValue match
          case "newline" => Success(TextSpan(start,end,CExpr.SLit(LitValue.LChar('\n'))))
          case "space" => Success(TextSpan(start,end,CExpr.SLit(LitValue.LChar(' '))))
          case "tab" => Success(TextSpan(start,end,CExpr.SLit(LitValue.LChar('\t'))))
          case "return" => Success(TextSpan(start,end,CExpr.SLit(LitValue.LChar('\r'))))
          case _ => Failure(UnsupportedCharacter(this.lexString.pos,strValue))
      }
      case None => Failure(ErrorEOF(this.lexString.pos))
  }

  private def parseVector():Try[TextSpan[CExpr]] = Try {
    val start = this.lexString.pos
    val exprList = this.readList(']').get
    val end = this.lexString.pos
    val expr = CExpr.SVector(exprList)
    TextSpan(start,end,expr)
  }

  private def parseList():Try[TextSpan[CExpr]] = Try {
    val start = this.lexString.pos
    val exprList = this.readList(')').get
    val end = this.lexString.pos
    val expr = CExpr.SList(exprList)
    TextSpan(start,end,expr)
  }

  private def parseMap():Try[TextSpan[CExpr]] = Try {
    val start = this.lexString.pos
    val exprList = this.readList('}').get
    val end = this.lexString.pos
    val expr = CExpr.SMap(exprList)
    TextSpan(start,end,expr)
  }

  private def parseXML():Try[TextSpan[CExpr]] = Try {
    val start = this.lexString.pos
    val fstChar = this.lexString.lookahead(1)
    if(fstChar.isEmpty || !CharUtils.isXMLSymStart(fstChar.get)) {
      throw InvalidXMLTag(this.lexString.pos,fstChar.getOrElse(' '))
    }
    val xmlTag = this.lexString.takeWhile(chr => CharUtils.isXMLSym(chr))
    val xmlTagName = xmlTag.getOrElse(throw InvalidXMLTag(this.lexString.pos,' '))
    var attrList:ArrayBuffer[(String,CExpr)] = ArrayBuffer.empty
    var isSingleXml = false
    boundary(while(true) {
      this.skipWhitespace()
      val nextChar = this.lexString.lookahead(1)
      if(nextChar.isEmpty) break()
      if(nextChar.get == '/') {
        if(this.lexString.lookahead(2) == Some('>')) {
          this.lexString.next()
          this.lexString.next()
          isSingleXml = true
          break()
        }
      } else if(nextChar.get == '>') {
        isSingleXml = false
        this.lexString.next()
        break()
      } else {
        if(CharUtils.isXMLSymStart(nextChar.get)) {
          val attrName = this.parseXMLSymbol().get
          val mbEq = this.lexString.lookahead(1)
          if(mbEq == Some('=')) {
            this.lexString.next()
          } else { throw XMLAttrMustPair(this.lexString.pos,attrName) }
          val attrValue = this.parse().get
          attrList.addOne((attrName,attrValue.value))
        } else {
          throw InvalidXMLAttrKey(this.lexString.pos,nextChar.get)
        }
      }
    })
    
    if(isSingleXml) {
      val end = this.lexString.pos
      TextSpan(start,end,CExpr.SXMLElement(xmlTagName.toString(),attrList,ArrayBuffer.empty))
    } else {
      var childList:ArrayBuffer[CExpr] = ArrayBuffer.empty
      boundary(while(true) {
        this.skipWhitespace()
        val lk1 = this.lexString.lookahead(1)
        val lk2 = this.lexString.lookahead(2)
       
        if(lk1 == Some('<') && lk2 == Some('/')) {
          this.lexString.next()
          this.lexString.next()
          this.lexString.dropWhile(CharUtils.isXMLSym)
          this.lexString.next()
          break()
        }
        val expr = this.parse().get
        childList += expr.value
      })
      val end = this.lexString.pos
      TextSpan(start,end,CExpr.SXMLElement(xmlTagName.toString(),attrList,childList))
    }
  }

  private def parseXMLSymbol():Try[String] = Try {
    val fstChar = this.lexString.lookahead(1)
    if(fstChar.isEmpty || !CharUtils.isXMLSymStart(fstChar.get)) {
      throw InvalidXMLTag(this.lexString.pos,fstChar.getOrElse(' '))
    }
    this.lexString.takeWhile(chr => CharUtils.isXMLSym(chr)).map(_.toString)
                  .getOrElse(throw InvalidXMLTag(this.lexString.pos,' '))
  }

  private def readList(endChar:Char):Try[ArrayBuffer[TextSpan[CExpr]]] = Try {
    val lst:ArrayBuffer[TextSpan[CExpr]] = ArrayBuffer.empty
    var isRun = true
    while(isRun) {
      this.skipWhitespace()
      val nextChar = this.lexString.lookahead(1)
      nextChar match
        case Some(chr) => {
          if(chr == endChar) {
            this.lexString.next()
            isRun = false
          } else {
            lst += this.parse().get
          }
        }
        case None => throw ErrorEOF(this.lexString.pos)
    }
    lst
  }

  private def isHexDigit(chr:Char):Boolean = {
    if(chr.isDigit) return true
    chr >= 'a' && chr <= 'z' || chr >= 'A' && chr <= 'Z'
  }
  
  private def parseNumber(chr1:Char,isNeg:Boolean):Try[TextSpan[CExpr]] = Try {
    val start = this.lexString.pos
    val chr2 = this.lexString.lookahead(1)
    (chr1,chr2) match
      //hex
      case('0',Some('x')) => {
        this.lexString.next()
        val hex = this.lexString.takeWhile(isHexDigit).getOrElse("")
        if(hex == "") throw ErrExpectedHex(this.lexString.pos)
        var hexNumber = JLong.parseLong(hex.toString(),16)
        val end = this.lexString.pos
        val expr = CExpr.SLit(LitValue.LLong(hexNumber))
        if(isNeg) hexNumber = -hexNumber
        TextSpan(start,end,expr)
      }
      case _ => {
        val mbInt1 = this.parseInteger1(chr1).get
        val mbFraction = this.fraction().get
        (mbInt1,mbFraction) match
          case (Some(sInt),None) => {
            val intValue = JInteger.parseInt(sInt)
            val e = this.exponent().get
            e match {
              case Some(exp) => {
                val mbF = sciToF64(intValue, exp.toInt);
                if(mbF.isEmpty) { throw ErrNumberOutOfRange(this.lexString.pos) }
                val end = this.lexString.pos
                val expr = CExpr.SLit(LitValue.LFloat(if(isNeg) -mbF.get else mbF.get))
                TextSpan(start,end,expr)
              }
              case None => { 
                val end = this.lexString.pos
                val expr = CExpr.SLit(LitValue.LLong(if(isNeg) -intValue else intValue))
                TextSpan(start,end,expr)  
              }
            }
          }
          case (Some(sInt),Some(frac)) => {
            val fStr = s"${sInt}.${frac}"
            val dvalue = JDouble.parseDouble(fStr)
            val mbE = this.exponent().get
            mbE match {
              case Some(e) => {
                val valf = sciToF64(dvalue, e.toInt);
                if(valf.isEmpty) throw ErrNumberOutOfRange(this.lexString.pos)
                val end = this.lexString.pos
                val expr = CExpr.SLit(LitValue.LFloat(if(isNeg) -valf.get else valf.get))
                TextSpan(start,end,expr)
              }
              case None => {
                val end = this.lexString.pos
                val expr = CExpr.SLit(LitValue.LFloat(if(isNeg) -dvalue else dvalue))
                TextSpan(start,end,expr)
              }
            }
          }
          case _ => throw ErrLexeme(this.lexString.pos)
      }
  }

  private def parseInteger1(chr:Char):Try[Option[String]] = {
    chr match
      case '0' => {
        val nextChr = this.lexString.lookahead(1)
        if(nextChr.isDefined && nextChr.get.isDigit) {
          return Failure(ErrLeadingZero(this.lexString.pos))
        }
        return Success(Some("0"))
      }
      case other => {
         if(other.isDigit) {
          val numString = this.lexString.takeWhile(_.isDigit)
          val sb = numString.getOrElse(StringBuilder())
          sb.insert(0,chr)
          return Success(Some(sb.toString()))
         } else { return Success(None) }
      }
  }

  protected def parseInteger():Try[Option[String]] = {
    val mbChr = this.lexString.lookahead(1);
    mbChr match {
      case Some('0') => {
        this.lexString.next()
        val mbChr2 = this.lexString.lookahead(1)
        if(mbChr2.isDefined && mbChr2.get.isDigit) {
          return Failure(ErrLeadingZero(this.lexString.pos))
        } else {
          return Success(Some("0"))
        }
      }
      case Some(value) if value.isDigit => {
        Success(this.lexString.takeWhile(_.isDigit).map(_.toString()))
      }
      case _ => Success(None) 
    }
  }

  private def fraction():Try[Option[String]] = {
    val chr1 = this.lexString.lookahead(1)
    chr1 match 
      case Some('.') => {
        this.lexString.next()
        val nums = this.lexString.takeWhile(_.isDigit)
        if(nums.isDefined) {
          Success(Some(nums.get.toString()))
        } else {
          this.lexString.putBack('.')
          Success(None)
        }
      }
      case _ => Success(None)
  }

  private def exponent():Try[Option[Long]] = Try {
    val mbChr = this.lexString.lookahead(1);
    mbChr match {
      case Some('e') => {
        this.lexString.next()
        val mbChr2 = this.lexString.lookahead(1)
        val isNeg = mbChr2 match {
          case Some('-') => { this.lexString.next(); true }
          case Some('+') => { this.lexString.next(); false }
          case _ => { false }
        }
        val mbInt = this.parseInteger().get
        mbInt match {
          case Some(v) => {
            val intValue = if(isNeg) { -JLong.parseLong(v) } else { JLong.parseLong(v) }
            Some(intValue)
          }
          case None => { throw ErrExpectedExponent(this.lexString.pos) }
        }
      }
      case _ => None
    }
  }

  private def sciToF64(c:Double,e:Int):Option[Double] = {
    if(c == 0) return Some(0f)
    if(e > 63 || e < -63) { return None }
    val d10:Double = 10d
    val de:Double = e
    val dc:Double = c
    if(e < 0) {
      Some(dc / Math.pow(d10,-de))      
    } else {
      Some(dc * Math.pow(d10,de))
    }
  }

  private def parseSymbol(chrStart:Char):Try[TextSpan[CExpr]] = Try {
    val start = this.lexString.pos
    if(!CharUtils.isSymCharStart(chrStart)) {
      throw InvalidSymbolChar(this.lexString.pos,chrStart)
    }
    if(chrStart == ':') {
      this.parseKeyworld().get
    } else {
      var isNS = false
      var nsName:StringBuilder = StringBuilder()
      var lastName:StringBuilder = StringBuilder()
      lastName.append(chrStart)
      var curLookChar = this.lexString.lookahead(1)
      boundary(while(curLookChar.isDefined) {
        
        if(curLookChar.isDefined && (curLookChar.get.isWhitespace || !CharUtils.isSymChar(curLookChar.get))) {
          break()
        }
        if(curLookChar.get == '/') {
          if(isNS) throw ErrSymbol(this.lexString.pos,nsName.toString())
          nsName.append(lastName)
          lastName.clear()
          isNS = true
          this.lexString.next()
        } else {
          lastName.append(curLookChar.get)
          this.lexString.next()
        }
        curLookChar = this.lexString.lookahead(1)
      })
      val lastString = lastName.toString()
      val end = this.lexString.pos
      val langSym = if(nsName.length() == 0) {
        lastString match
          case "nil" => Some(TextSpan(start,end,CExpr.Nil))
          case "true" => Some(TextSpan(start,end,CExpr.SLit(LitValue.LBool(true))))
          case "false" => Some(TextSpan(start,end,CExpr.SLit(LitValue.LBool(false))))
          case _ => None
      } else { None }
      langSym match
        case Some(value) => value
        case None => {
          val expr = CExpr.SSymbol(if(isNS) Some(nsName.toString()) else None,lastString)
          TextSpan(start,end,expr)
        }
    }
  }

  private def parseKeyworld():Try[TextSpan[CExpr]] = Try {
    val start = this.lexString.pos
    val takeString = this.lexString.takeWhile(chr => !chr.isWhitespace && CharUtils.isSymChar(chr) && chr != '/' )
    takeString.foreach(sb => sb.insert(0,':'))
    val allString = takeString.map(_.toString).getOrElse("")
    if(allString.length() == 1 || allString == "::" || allString.endsWith(":") || allString.startsWith(":::")) {
      throw ErrSymbol(this.lexString.pos,allString)
    }
    var isLocal = false
    if(allString.startsWith("::")) {
      isLocal = true
    }
    val end = this.lexString.pos
    val expr = CExpr.SKeyworld(allString,isLocal)
    TextSpan(start,end,expr)
  }


}

object Parser {
  def fromSource(sourceName:String,codeSource:Source):Parser = {
    val lexString = LexString(codeSource)
    new Parser(sourceName,lexString)
  }
}