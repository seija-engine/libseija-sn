package slog.message
import slog.LoggableMessage

trait Message[M] extends LoggableMessage {
  override def value: M
}