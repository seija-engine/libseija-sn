package sxml.compiler
import scala.util.Try
import sxml.vm.CompiledModule
import sxml.parser.CExpr

case class Compiler() {
    def compileExpr(expr:CExpr):Try[CompiledModule] = {
        ???
    }
}