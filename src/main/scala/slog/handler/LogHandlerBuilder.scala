package slog.handler

import slog.format.Formatter
import slog.writer.Writer
import slog.writer.ConsoleWriter
import slog.output.format.OutputFormat
import slog.LogRecord
import slog.modify.LogModifier

case class LogHandlerBuilder(formatter: Formatter = Formatter.default,
                             writer: Writer = ConsoleWriter,
                             outputFormat: OutputFormat = OutputFormat.default,
                             modifiers: List[LogModifier] = Nil,
                             handle: LogHandle = SynchronousLogHandle) extends LogHandler {
  override def log(record: LogRecord): Unit = handle.log(this, record)

  def withFormatter(formatter: Formatter): LogHandlerBuilder = copy(formatter = formatter)

  def withWriter(writer: Writer): LogHandlerBuilder = copy(writer = writer)

  def withModifiers(modifiers: LogModifier*): LogHandlerBuilder = copy(modifiers = modifiers.toList ::: this.modifiers)

  def withLogHandle(handle: LogHandle): LogHandlerBuilder = copy(handle = handle)
}