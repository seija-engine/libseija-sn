package com.seija.slog
import com.seija.slog.output.LogOutput

trait Loggable[-T] {
  def apply(value: T): LogOutput
}