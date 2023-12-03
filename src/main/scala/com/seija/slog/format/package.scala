package com.seija.slog

import com.seija.slog.output.LogOutput
import com.seija.slog.output.EmptyOutput
import com.seija.slog.output.CompositeOutput
import com.seija.slog.output.BoldOutput
import com.seija.slog.output.ColoredOutput
import com.seija.slog.output.Color
import scala.collection.mutable.ListBuffer

package object format {
    def date: FormatBlock = FormatBlock.Date.Standard
    def dateFull: FormatBlock = FormatBlock.Date.Full
    def newLine: FormatBlock = FormatBlock.NewLine
    def multiLine(blocks: FormatBlock*): FormatBlock = new MultiLine(blocks = blocks.toList)

    def bold(block: FormatBlock): FormatBlock = FormatBlock { logRecord =>
        new BoldOutput(block.format(logRecord))
    }

    def fg(color: Color, block: FormatBlock): FormatBlock = FormatBlock { logRecord =>
        new ColoredOutput(color, block.format(logRecord))
    }

    def cyan(block: FormatBlock): FormatBlock = fg(Color.Cyan, block)
    def green(block: FormatBlock): FormatBlock = fg(Color.Green, block)

    def string(value: String): FormatBlock = FormatBlock.RawString(value)

    def messages: FormatBlock = FormatBlock.Messages

    lazy val space: FormatBlock = string(" ")
    lazy val openBracket: FormatBlock = string("[")
    lazy val closeBracket: FormatBlock = string("]")

    def level: FormatBlock = FormatBlock.Level

    def position: FormatBlock = FormatBlock.Position

    def levelColored: FormatBlock = FormatBlock { logRecord =>
        val color = logRecord.level match {
            case Level.Trace => Color.White
            case Level.Debug => Color.Green
            case Level.Info => Color.Blue
            case Level.Warn => Color.Yellow
            case Level.Error => Color.Red
            case Level.Fatal => Color.Magenta
            case _ => Color.Cyan
        }
        new ColoredOutput(color, level.format(logRecord))
    }
    
    def levelColor(block: FormatBlock): FormatBlock = FormatBlock { logRecord =>
        val color = logRecord.level match {
            case Level.Trace => Color.White
            case Level.Debug => Color.Green
            case Level.Info => Color.Blue
            case Level.Warn => Color.Yellow
            case Level.Error => Color.Red
            case Level.Fatal => Color.Magenta
            case _ => Color.Cyan
        }
        new ColoredOutput(color, block.format(logRecord))
    }

    def groupBySecond(blocks: FormatBlock*): FormatBlock = {
        var lastId: Long = 0L
        var lastThreadName: String = ""
        var lastTime: Long = 0L
        var lastLevel: Level = Level.Trace
        var lastClassName: String = ""
        var lastMethodName: Option[String] = None
        var lastLineNumber: Option[Int] = None
        var previousOutput: Option[LogOutput] = None
        FormatBlock { logRecord =>
            val distance = logRecord.timeStamp - lastTime
            val level = logRecord.level
            val cn = logRecord.className
            val mn = logRecord.methodName
            val ln = logRecord.line
            if (lastId == logRecord.id && previousOutput.nonEmpty) {
                previousOutput.getOrElse(sys.error("Previous output is None"))
            } else if (distance <= 1000L && level == lastLevel && cn == lastClassName && mn == lastMethodName && ln == lastLineNumber) {
                previousOutput = None
                EmptyOutput
            } else {
                lastId = logRecord.id
                lastTime = logRecord.timeStamp
                lastLevel = level
                lastClassName = cn
                lastMethodName = mn
                lastLineNumber = ln
                val output = new CompositeOutput(blocks.map(_.format(logRecord)).toList)
                previousOutput = Some(output)
                output
            }
        }
    }

    implicit class FormatterInterpolator(val sc: StringContext) extends AnyVal {
    def formatter(args: Any*): Formatter = {
      val list = ListBuffer.empty[FormatBlock]
      val argsVector = args.toVector.asInstanceOf[Vector[FormatBlock]]
      sc.parts.zipWithIndex.foreach {
        case (part, index) => {
          if (part.nonEmpty) {
            list += string(part)
          }
          if (index < argsVector.size) {
            list += argsVector(index)
          }
        }
      }
      Formatter.fromBlocks(list.toList: _*)
    }
  }

}
