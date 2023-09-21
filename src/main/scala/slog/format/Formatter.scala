package slog.format
import slog.LogRecord
import slog.output.LogOutput

trait Formatter {
  def format(record: LogRecord): LogOutput
}

object Formatter {
    lazy val advanced: Formatter = Formatter.fromBlocks(
      groupBySecond(
        cyan(bold(dateFull)),
        space,
        space,
        levelColored,
        space,
        green(position),
        newLine
      ),
      multiLine(messages),
    )

    lazy val compact: Formatter = formatter"$date ${string("[")}$levelColored${string("]")} ${green(position)} - $messages"
    lazy val colored: Formatter = formatter"${levelColor(messages)}"

    var default: Formatter = compact

    def fromBlocks(blocks: FormatBlock*): Formatter = new FormatBlocksFormatter(blocks.toList)
}