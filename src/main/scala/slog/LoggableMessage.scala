package slog

import slog.output.LogOutput
import slog.output.TextOutput
import slog.message.LazyMessage
import slog.Loggable

trait LoggableMessage extends LogFeature {
  def value: Any
  def logOutput: LogOutput

  override def apply(record: LogRecord): LogRecord = record.withMessages(this)
}

object LoggableMessage {
  implicit def string2LoggableMessage(s: => String): LoggableMessage = LoggableMessage[String](new TextOutput(_))(s)

  def apply[V](toLogOutput: V => LogOutput)(value: => V): LoggableMessage =
    new LazyMessage[V](() => value)(new Loggable[V] {
      override def apply(value: V): LogOutput = toLogOutput(value)
  })
}