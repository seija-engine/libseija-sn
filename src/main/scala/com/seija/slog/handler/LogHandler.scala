package com.seija.slog.handler

import com.seija.slog.LogRecord
import com.seija.slog.format.Formatter
import com.seija.slog.writer.Writer
import com.seija.slog.writer.ConsoleWriter
import com.seija.slog.Level
import com.seija.slog.modify.LogModifier
import com.seija.slog.output.format.OutputFormat

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