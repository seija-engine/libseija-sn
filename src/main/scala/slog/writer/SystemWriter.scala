package slog.writer
import scala.collection.mutable
import slog.LogRecord
import slog.output.LogOutput
import slog.output.format.OutputFormat
import slog.Logger
import java.io.PrintStream
import scala.math.Ordering.Implicits._
import slog.Level

object SystemWriter extends Writer {
 
  var alwaysFlush: Boolean = false

  val DefaultStringBuilderStartCapacity: Int = 512

  var stringBuilderStartCapacity: Int = DefaultStringBuilderStartCapacity

  private val stringBuilders:mutable.StringBuilder = new mutable.StringBuilder(stringBuilderStartCapacity)

  override def write(record: LogRecord, output: LogOutput, outputFormat: OutputFormat): Unit = {
    val stream = if (record.level <= Level.Info) {
      Logger.system.out
    } else {
      Logger.system.err
    }
    write(stream, output, outputFormat)
  }

  def write[M](stream: PrintStream, output: LogOutput, outputFormat: OutputFormat): Unit = {
    val sb = stringBuilders
    outputFormat.begin(sb.append(_))
    outputFormat(output, s => sb.append(s))
    outputFormat.end(sb.append(_))
    stream.println(sb.toString())
    if (alwaysFlush) stream.flush()
    sb.clear()
  }
}