package com.seija.slog.format

import com.seija.slog.LogRecord
import com.seija.slog.output.LogOutput
import com.seija.slog.output.CompositeOutput
import com.seija.slog.output.TextOutput


trait FormatBlock {
  def format(record: LogRecord): LogOutput
}

object FormatBlock {
    def apply(f: LogRecord => LogOutput): FormatBlock = new FormatBlock {
        override def format(record: LogRecord): LogOutput = f(record)
    }

    object Date {
      object Standard extends CachingFormatBlock {
        override protected def cacheLength: Long = 1000L

        override protected def formatCached(record: LogRecord): LogOutput = {
          val l = record.timeStamp
          val t = com.seija.core.Date(l)
          val d = s"${t.Y}.${t.m}.${t.d} ${t.T}"
          new TextOutput(d)
        }
      }
      object  Full extends FormatBlock {
        override def format(record: LogRecord): LogOutput = {
          val l:Long = record.timeStamp
          val t = com.seija.core.Date(l)
          val d = s"${t.Y}.${t.m}.${t.d} ${t.T}:${t.L}"
          new TextOutput(d)
        }
      }

      
    }

    case class RawString(s: String) extends FormatBlock {
      override def format(record: LogRecord): LogOutput = new TextOutput(s)
    }
    
    object Level extends FormatBlock {
      override def format(record: LogRecord): LogOutput = new TextOutput(record.level.name)
      object PaddedRight extends FormatBlock {
        override def format(record: LogRecord): LogOutput = new TextOutput(record.level.namePadded)
      }
    }

    object LineNumber extends FormatBlock {
      override def format(record: LogRecord): LogOutput = new TextOutput(record.line.fold("")(_.toString))
    }

    object ColumnNumber extends FormatBlock {
      override def format(record: LogRecord): LogOutput = new TextOutput(record.column.fold("")(_.toString))
    }
    
    object MethodName extends FormatBlock {
      override def format(record: LogRecord): LogOutput = new TextOutput(record.methodName.getOrElse(""))
    }
    
    object ClassNameSimple extends FormatBlock {
      override def format(record: LogRecord): LogOutput = {
        val cn = record.className
        val index = cn.lastIndexOf('.')
        val simple = if (index != -1) {
          cn.substring(index + 1)
        } else {
          cn
        }
        new TextOutput(simple)
      }
    }

    object ClassAndMethodNameSimple extends FormatBlock {
      override def format(record: LogRecord): LogOutput = {
        val className = ClassNameSimple.format(record).plainText
        val methodName = if (record.methodName.nonEmpty) {
          s".${MethodName.format(record).plainText}"
        } else {
          ""
        }
        new TextOutput(s"$className$methodName")
      }
    }
    
    object ClassName extends FormatBlock {
      override def format(record: LogRecord): LogOutput = new TextOutput(record.className)
    }

    object ClassAndMethodName extends FormatBlock {
      override def format(record: LogRecord): LogOutput = {
        val className = ClassName.format(record).plainText
        val methodName = if (record.methodName.nonEmpty) {
          s".${MethodName.format(record).plainText}"
        } else {
          ""
        }
        new TextOutput(s"$className$methodName")
      }
    }

    object Position extends FormatBlock {
      override def format(record: LogRecord): LogOutput = {
        val line = if (record.line.nonEmpty) {
          s":${LineNumber.format(record).plainText}"
        } else {
          ""
        }
        
        val column = if (record.column.nonEmpty) {
          s":${ColumnNumber.format(record).plainText}"
        } else {
          ""
        }
        new TextOutput(s"${ClassAndMethodName.format(record).plainText}$line$column")
      }
    }

    object NewLine extends FormatBlock {
      override def format(record: LogRecord): LogOutput = new TextOutput(System.lineSeparator)
    }
    
    object Messages extends FormatBlock {
      override def format(record: LogRecord): LogOutput = record.logOutput
    }

}



case class MultiLine(maxChars: () => Int = MultiLine.PlatformColumns, prefix: String = "    ", blocks: List[FormatBlock]) extends FormatBlock {
    override def format(record: LogRecord): LogOutput = {
      val pre = new TextOutput(prefix)
      val max = maxChars() - prefix.length
      val newLine = FormatBlock.NewLine.format(record)
      val outputs = MultiLine.splitNewLines(blocks.map(_.format(record)))
      val list = outputs.flatMap { output =>
        var current = output
        var list = List.empty[LogOutput]
        while (current.length > max) {
          val (left, right) = current.splitAt(max)
          list = list ::: List(pre, left, newLine)
          current = right
        }
        list = list ::: List(pre, current)
        list
      }
      new CompositeOutput(list)
    }
}

object MultiLine {
    val DefaultMaxChars: Int = 120
    val PlatformColumns: () => Int = () => 120

    def splitNewLines(outputs: List[LogOutput]): List[LogOutput] = outputs.flatMap { output =>
      var lo = output
      var plainText = output.plainText
      var splitted = List.empty[LogOutput]
      def process(): Unit = {
        val index = plainText.indexOf('\n')
        if (index == -1) {
          splitted = lo :: splitted
          // Finished
        } else {
          val (one, two) = lo.splitAt(index + 1)
          splitted = one :: splitted
          lo = two
          plainText = plainText.substring(index + 1)
          process()
        }
      }
      process()
      splitted.reverse
    }
}