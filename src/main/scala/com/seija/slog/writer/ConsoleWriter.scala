package com.seija.slog.writer

import com.seija.slog.LogRecord
import com.seija.slog.output.LogOutput
import com.seija.slog.output.format.OutputFormat

object ConsoleWriter extends Writer {
  override def write(record: LogRecord, output: LogOutput, outputFormat: OutputFormat): Unit = {
    SystemWriter.write(record, output, outputFormat) 
  }
}