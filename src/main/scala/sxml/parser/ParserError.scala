package sxml.parser

case class ErrorEOF() extends Exception
case class ErrCharInGap() extends Exception
case class ErrExpectedHex() extends Exception
case class ErrLeadingZero() extends Exception
case class ErrExpectedExponent() extends Exception
case class ErrNumberOutOfRange() extends Exception
case class ErrLexeme() extends Exception