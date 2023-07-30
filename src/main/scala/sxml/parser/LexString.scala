package sxml.parser
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

case class LexPos(line:Int,col:Int)
class LexString(source:Source,maxCache:Int = 4) {
  private var curLine:Int = 0
  private var curCol:Int = 0

  private var aheadCount:Int = 0
  private var curIndex:Int = -1
  private var charCount:Int = 0
  private val cacheLst:ArrayBuffer[Char] = ArrayBuffer.empty

  private def subAheadLen:Int = this.cacheLst.length - this.aheadCount
  def next():Option[Char] = {
    val chr = this._next()
    if(chr.isDefined && chr.get == '\n') {
      this.curLine += 1
      this.curCol = 0
    } else {
      this.curCol += 1
    }
    chr
  }
  private def _next():Option[Char] = {
    if(this.aheadCount > 0) {
      this.curIndex += 1
      val chr = this.cacheLst(this.subAheadLen)
      this.aheadCount -= 1
      Some(chr)
    } else {
      if(this.source.hasNext) {
        val next = this.source.next()
        this.cacheLst.addOne(next)
        this.curIndex += 1
        if(this.cacheLst.length >= maxCache) {
          this.cacheLst.remove(0)
        }
        Some(next)
      } else None
    }
  }

  def lookahead(count:Int):Option[Char] = {
    if(this.aheadCount > count) {
      Some(this.cacheLst(this.subAheadLen + count - 1))
    } else {
      val addCount:Int = count - this.aheadCount
      for(_ <- 0.until(addCount)) {
        if(!this.source.hasNext) return None
        this.aheadCount += 1
        this.cacheLst.addOne(this.source.next())
      }
      val idx = this.subAheadLen + count - 1
      if(idx >= this.cacheLst.length || idx < 0) return None
      Some(this.cacheLst(idx))
    }
  }

  def skipWhitespace():Unit = {
    var curChr = this.lookahead(1)
    while(curChr.isDefined) {
      if(curChr.get.isWhitespace) {
        this.next()
        curChr = this.lookahead(1)
      } else { return }
    }
  }

  def takeWhile(f:Char => Boolean):Option[StringBuilder] = {
    val sb = StringBuilder()
    var chr = this.lookahead(1)
    while(chr.isDefined) {
      if(f(chr.get)) {
        sb.addOne(this.next().get)
        chr = this.lookahead(1)
      } else {
        return if(sb.isEmpty) None else Some(sb)
      }
    }
    if(sb.isEmpty) None else Some(sb)
  }

  def putBack(chr:Char):Unit = {
    this.cacheLst += chr
    this.aheadCount += 1
  }

  def pos:LexPos = LexPos(this.curLine,this.curCol)


}
