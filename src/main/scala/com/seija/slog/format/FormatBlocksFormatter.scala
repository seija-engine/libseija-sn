package com.seija.slog.format

import com.seija.slog.LogRecord
import com.seija.slog.output.LogOutput
import com.seija.slog.output.CompositeOutput

class FormatBlocksFormatter(blocks: List[FormatBlock]) extends Formatter {
  override def format(record: LogRecord): LogOutput = {
    new CompositeOutput(blocks.map(_.format(record)))
  }

  override def toString: String = s"blocks(${blocks.mkString(", ")})"
}