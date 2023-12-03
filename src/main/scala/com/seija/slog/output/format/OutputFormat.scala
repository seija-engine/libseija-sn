package com.seija.slog.output.format

import com.seija.slog.output.LogOutput

trait OutputFormat {
  def init(stream: String => Unit): Unit = {}

  def begin(stream: String => Unit): Unit = {}

  def apply(output: LogOutput, stream: String => Unit): Unit

  def end(stream: String => Unit): Unit = {}
}

object OutputFormat {
  var default: OutputFormat = ANSIOutputFormat
}