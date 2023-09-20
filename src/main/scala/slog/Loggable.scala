package slog
import slog.output.LogOutput

trait Loggable[-T] {
  def apply(value: T): LogOutput
}