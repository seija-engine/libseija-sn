package com.seija.slog.writer

import com.seija.slog.LogRecord
import com.seija.slog.output.LogOutput
import com.seija.slog.output.format.OutputFormat

trait Writer {
  def write(record: LogRecord, output: LogOutput, outputFormat: OutputFormat): Unit

  def dispose(): Unit = {}
}