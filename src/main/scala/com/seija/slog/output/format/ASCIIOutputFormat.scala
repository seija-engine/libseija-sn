package com.seija.slog.output.format

import com.seija.slog.output.LogOutput

object ASCIIOutputFormat extends OutputFormat {
  override def apply(output: LogOutput, stream: String => Unit): Unit = stream(output.plainText)
}
