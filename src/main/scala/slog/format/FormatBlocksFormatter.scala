package slog.format

import slog.LogRecord
import slog.output.LogOutput
import slog.output.CompositeOutput

class FormatBlocksFormatter(blocks: List[FormatBlock]) extends Formatter {
  override def format(record: LogRecord): LogOutput = {
    new CompositeOutput(blocks.map(_.format(record)))
  }

  override def toString: String = s"blocks(${blocks.mkString(", ")})"
}