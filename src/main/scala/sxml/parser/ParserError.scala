package sxml.parser

case class ErrorEOF() extends Exception
case class ErrCharInGap() extends Exception
case class ErrExpectedHex() extends Exception
case class ErrLeadingZero() extends Exception