package com.seija.sxml.compiler
import com.seija.sxml.parser.LexPos
import com.seija.sxml.parser.PosError
import com.seija.sxml.parser.SpanPos

sealed class TransError(pos:SpanPos) extends Exception {
    def Pos:SpanPos = this.pos
}

case class InvalidList(pos:SpanPos) extends TransError(pos)
case class InvalidIf(pos:SpanPos) extends TransError(pos)
case class InvalidMatch(pos:SpanPos) extends TransError(pos)
case class InvalidMatchKeyType(pos:SpanPos) extends TransError(pos)
case class InvalidDef(pos:SpanPos) extends TransError(pos)
case class InvalidFN(pos:SpanPos) extends TransError(pos)
case class InvalidDefFN(pos:SpanPos) extends TransError(pos)
case class InvalidLetFN(pos:SpanPos) extends TransError(pos)
case class InvalidDispatch(pos:SpanPos) extends TransError(pos)


sealed class CompileError(pos:SpanPos,errString:String = "") extends Exception(errString) {
    def Pos:SpanPos = this.pos
}
case class NotFoundSymbol(pos:SpanPos,symName:String) extends CompileError(pos,symName)
case class ErrMapCount(pos:SpanPos) extends CompileError(pos)
case class InvalidLet(pos:SpanPos) extends CompileError(pos)
case class InvalidRecur(pos:SpanPos) extends CompileError(pos)