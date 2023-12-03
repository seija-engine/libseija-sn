package com.seija.slog.handler

import com.seija.slog.LogRecord


object SynchronousLogHandle extends LogHandle {
  def log(handler: LogHandlerBuilder, record: LogRecord): Unit = {
    //record.modify(handler.modifiers).foreach { r =>
      val logOutput = handler.formatter.format(record)
      handler.writer.write(record, logOutput, handler.outputFormat)
    //}
  }
}