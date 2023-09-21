package slog.handler

import slog.LogRecord
import slog.format.Formatter
import slog.writer.Writer
import slog.writer.ConsoleWriter
import slog.Level
import slog.modify.LogModifier
import slog.output.format.OutputFormat

trait LogHandler {
  def log(record: LogRecord): Unit
}

object LogHandler {
  def apply(formatter: Formatter = Formatter.default,
            writer: Writer = ConsoleWriter,
            minimumLevel: Option[Level] = None,
            modifiers: List[LogModifier] = Nil,
            outputFormat: OutputFormat = OutputFormat.default,
            handle: LogHandle = SynchronousLogHandle): LogHandlerBuilder = {
    //val mods = (minimumLevel.map(l => (LevelFilter >= l).alwaysApply).toList ::: modifiers).sortBy(_.priority)
    LogHandlerBuilder(formatter, writer, outputFormat, Nil, handle)
  }

  //def apply(minimumLevel: Level)(f: LogRecord => Unit): FunctionalLogHandler = {
  //  FunctionalLogHandler(f, List(LevelFilter >= minimumLevel))
  //}
}