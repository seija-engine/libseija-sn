package sxml.parser
import scala.io.Source
import scala.util.{Failure, Success, Try}
import scala.collection.mutable.ArrayBuffer
import java.lang.Long as JLong
import java.lang.Integer as JInteger
import java.lang.Double as JDouble
import input.KeyCode.D
case class TextSpan[T](start:LexPos,end:LexPos,value:T)


class Parser(lexString: LexString) {
  def parseALL():ArrayBuffer[TextSpan[CExpr]] = {
    var curExpr:Try[TextSpan[CExpr]] = this.parse()
    val exprList:ArrayBuffer[TextSpan[CExpr]] = ArrayBuffer.empty
    while(curExpr.isSuccess) {
      exprList += curExpr.get
      println(curExpr.get)
      if(this.lexString.lookahead(1).isEmpty) {
        return exprList
      }
      curExpr = this.parse()
      if(curExpr.isFailure) {
        System.err.println(curExpr.failed.get)
      }
    }
    exprList
  }

  private def parse():Try[TextSpan[CExpr]] = {
    this.lexString.skipWhitespace()
    val curChr = this.lexString.next()
    if(curChr.isDefined) {
      val ret = curChr.get match
        case '"' => this.parseString()
        case ';' => this.parseComment()
        case '(' => ???
        case '[' => ???
        case '-' => {
          val nchr = this.lexString.lookahead(1);
          if(nchr.isDefined && nchr.get.isDigit) {
            this.lexString.next()
            return this.parseNumber(nchr.get,true)
          }
          ???
        }
        case  chr  => {
          if(chr.isDigit) { return this.parseNumber(chr,false) }
          ???
        }
      return ret
    }
    Failure(ErrorEOF())
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
        case Some(_) => { return Failure(ErrCharInGap()) }
        case None => return Failure(ErrorEOF())
      if(normalString.isDefined) normalString = this.lexString.takeWhile(normalF)
    }
    val end = this.lexString.pos
    val expr = CExpr.SLit(LitValue.LString(outBuilder.toString()))
    Success(TextSpan(start, end, expr))
  }

  private def parseComment():Try[TextSpan[CExpr]] = {
    val start = this.lexString.pos
    val commentString = this.lexString.takeWhile(chr => chr != '\r' && chr != '\n').getOrElse("")
    val end = this.lexString.pos
    val expr = CExpr.SComment(commentString.toString())
    Success(TextSpan(start, end, expr))
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
        if(hex == "") throw ErrExpectedHex()
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
                if(mbF.isEmpty) { throw ErrNumberOutOfRange() }
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
                if(valf.isEmpty) throw ErrNumberOutOfRange()
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
          case _ => throw ErrLexeme()
      }
  }

  private def parseInteger1(chr:Char):Try[Option[String]] = {
    chr match
      case '0' => {
        val nextChr = this.lexString.lookahead(1)
        if(nextChr.isDefined && nextChr.get.isDigit) {
          return Failure(ErrLeadingZero())
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
          return Failure(ErrLeadingZero())
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
          case None => { throw ErrExpectedExponent() }
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

}

object Parser {
  def fromSource(codeSource:Source):Parser = {
    val lexString = LexString(codeSource)
    new Parser(lexString)
  }
}