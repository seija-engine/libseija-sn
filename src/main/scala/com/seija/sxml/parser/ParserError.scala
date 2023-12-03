package com.seija.sxml.parser
sealed class PosError(pos:LexPos) extends Exception {
    def Pos:LexPos = this.pos
}
case class ErrorEOF(pos:LexPos) extends PosError(pos)
case class ErrCharInGap(pos:LexPos) extends PosError(pos)
case class ErrExpectedHex(pos:LexPos) extends PosError(pos)
case class ErrLeadingZero(pos:LexPos) extends PosError(pos)
case class ErrExpectedExponent(pos:LexPos) extends PosError(pos)
case class ErrNumberOutOfRange(pos:LexPos) extends PosError(pos)
case class ErrLexeme(pos:LexPos) extends PosError(pos)
case class UnsupportedCharacter(pos:LexPos,value:String) extends PosError(pos)
case class InvalidSymbolChar(pos:LexPos,chr:Char) extends PosError(pos)
case class ErrSymbol(pos:LexPos,value:String) extends PosError(pos)
case class InvalidXMLTag(pos:LexPos,chr:Char) extends PosError(pos)
case class XMLAttrMustPair(pos:LexPos,attr:String) extends PosError(pos)
case class InvalidXMLAttrKey(pos:LexPos,value:Char) extends PosError(pos)