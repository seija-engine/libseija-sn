package slog.handler

import slog.LogRecord

trait LogHandler {
  def log(record: LogRecord): Unit
}