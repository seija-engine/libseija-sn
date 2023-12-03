package com.seija.slog.handler

import com.seija.slog.LogRecord

trait LogHandle {
  def log(handler: LogHandlerBuilder, record: LogRecord): Unit
}