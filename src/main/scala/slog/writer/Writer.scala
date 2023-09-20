package slog.writer

import slog.LogRecord
import slog.output.LogOutput
import slog.output.format.OutputFormat

trait Writer {
  def write(record: LogRecord, output: LogOutput, outputFormat: OutputFormat): Unit

  def dispose(): Unit = {}
}