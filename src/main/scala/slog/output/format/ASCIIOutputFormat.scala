package slog.output.format

import slog.output.LogOutput

object ASCIIOutputFormat extends OutputFormat {
  override def apply(output: LogOutput, stream: String => Unit): Unit = stream(output.plainText)
}
