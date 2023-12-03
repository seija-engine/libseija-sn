package com.seija.slog.message
import com.seija.slog.LoggableMessage

trait Message[M] extends LoggableMessage {
  override def value: M
}