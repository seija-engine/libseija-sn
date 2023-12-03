package com.seija.slog.message
import com.seija.slog.Loggable
import com.seija.slog.output.LogOutput

class LazyMessage[M](function: () => M)
                    (implicit loggable: Loggable[M]) extends Message[M] {
  override lazy val value: M = function()
  override lazy val logOutput: LogOutput = loggable(value)
}