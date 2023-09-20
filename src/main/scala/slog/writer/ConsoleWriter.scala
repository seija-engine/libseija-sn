package slog.writer

import slog.LogRecord
import slog.output.LogOutput
import slog.output.format.OutputFormat

object ConsoleWriter extends Writer {
  override def write(record: LogRecord, output: LogOutput, outputFormat: OutputFormat): Unit = {
    SystemWriter.write(record, output, outputFormat) 
  }
}