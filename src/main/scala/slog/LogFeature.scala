package slog
import slog.LoggableMessage;

trait LogFeature {
  def apply(record: LogRecord): LogRecord
}

object LogFeature {
  def apply(f: LogRecord => LogRecord): LogFeature = (record: LogRecord) => f(record)

  implicit def string2LoggableMessage(s: => String): LogFeature = LoggableMessage.string2LoggableMessage(s)
  implicit def any2LoggableMessage(s: => Any): LogFeature = LoggableMessage.string2LoggableMessage(s.toString)
  //implicit def logOutput2LoggableMessage(lo: => LogOutput): LogFeature = LoggableMessage[LogOutput](identity)(lo)
  //implicit def throwable2LoggableMessage(throwable: => Throwable): LogFeature = TraceLoggableMessage(throwable)
}