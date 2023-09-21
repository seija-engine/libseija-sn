package slog.handler

import slog.LogRecord

trait LogHandle {
  def log(handler: LogHandlerBuilder, record: LogRecord): Unit
}