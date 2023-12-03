package com.seija.sxml.parser

object CharUtils {
  def isSymChar(chr:Char):Boolean = !"\";@^`~()[]{}\\".exists(_ == chr)
  def isSymCharStart(chr:Char):Boolean = !"\";@^`~()[]{}\\#".exists(_ == chr)
  def isXMLSymStart(chr:Char):Boolean = chr.isLetter || chr == '_'
  def isXMLSym(chr:Char):Boolean = chr.isLetterOrDigit || chr == '_' || chr == '.'
}
