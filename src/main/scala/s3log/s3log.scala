package s3log

import scala.quoted.Quotes
import scala.language.experimental.macros
import scala.quoted.Expr

case class LogConfig(final val enable:Boolean)

package object s3log {

    final val EnableLog = false

    inline def log(msg: => String) = inline if(EnableLog) log_(msg)

    inline def log_(msg: => String) =  ${ logImpl('msg) }

    def logImpl(msg:Expr[String])(using q:Quotes):Expr[Unit] = {
        import q.reflect.*
        val line:Int = Position.ofMacroExpansion.startLine
        val fileName:String = Position.ofMacroExpansion.sourceFile.getJPath.map(_.getFileName().toString()).getOrElse("")
        val repr = '{
            val lineValue = ${Expr(line)} 
            val fileValue = ${Expr(fileName)}
            val msgValue = ${msg}
            println(s"[${fileValue}:${lineValue}] ${msgValue}") 
        }
        report.info(repr.show)
        repr
    }
}