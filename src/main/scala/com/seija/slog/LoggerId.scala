package com.seija.slog

final case class LoggerId(value: Long) extends AnyVal

object LoggerId {
  private var counter = 0L

  def apply(): LoggerId = {
    counter += 1
    new LoggerId(counter)
  }
}