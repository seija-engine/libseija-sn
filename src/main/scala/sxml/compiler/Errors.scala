package sxml.compiler
import sxml.parser.LexPos
import sxml.parser.PosError
import sxml.parser.SpanPos

sealed class TransError(pos:SpanPos) extends Exception {
    def Pos:SpanPos = this.pos
}

case class InvalidList(pos:SpanPos) extends TransError(pos)
case class InvalidIf(pos:SpanPos) extends TransError(pos)
case class InvalidDef(pos:SpanPos) extends TransError(pos)