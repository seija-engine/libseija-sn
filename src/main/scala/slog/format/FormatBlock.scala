package slog.format

import slog.LogRecord
import slog.output.LogOutput
import slog.output.CompositeOutput
import slog.output.TextOutput

trait FormatBlock {
  def format(record: LogRecord): LogOutput
}

object FormatBlock {
    def apply(f: LogRecord => LogOutput): FormatBlock = new FormatBlock {
        override def format(record: LogRecord): LogOutput = f(record)
    }
}

object NewLine extends FormatBlock {
    override def format(record: LogRecord): LogOutput = new TextOutput(System.lineSeparator)
}

case class MultiLine(maxChars: () => Int = MultiLine.PlatformColumns, prefix: String = "    ", blocks: List[FormatBlock]) extends FormatBlock {
    override def format(record: LogRecord): LogOutput = {
      val pre = new TextOutput(prefix)
      val max = maxChars() - prefix.length
      val newLine = NewLine.format(record)
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