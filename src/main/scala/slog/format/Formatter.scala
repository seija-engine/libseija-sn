package slog.format
import slog.LogRecord
import slog.output.LogOutput

trait Formatter {
  def format(record: LogRecord): LogOutput
}

object Formatter {
  
    var default: Formatter = ???

    def fromBlocks(blocks: FormatBlock*): Formatter = new FormatBlocksFormatter(blocks.toList)
}