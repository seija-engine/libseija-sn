package slog

import slog.output.LogOutput
import slog.output.EmptyOutput
import slog.output.CompositeOutput

package object format {
    def groupBySecond(blocks: FormatBlock*): FormatBlock = {
        var lastId: Long = 0L
        var lastThreadName: String = ""
        var lastTime: Long = 0L
        var lastLevel: Level = Level.Trace
        var lastClassName: String = ""
        var lastMethodName: Option[String] = None
        var lastLineNumber: Option[Int] = None
        var previousOutput: Option[LogOutput] = None
        FormatBlock { logRecord =>
            val distance = logRecord.timeStamp - lastTime
            val level = logRecord.level
            val cn = logRecord.className
            val mn = logRecord.methodName
            val ln = logRecord.line
            if (lastId == logRecord.id && previousOutput.nonEmpty) {
                previousOutput.getOrElse(sys.error("Previous output is None"))
            } else if (distance <= 1000L && level == lastLevel && cn == lastClassName && mn == lastMethodName && ln == lastLineNumber) {
                previousOutput = None
                EmptyOutput
            } else {
                lastId = logRecord.id
                lastTime = logRecord.timeStamp
                lastLevel = level
                lastClassName = cn
                lastMethodName = mn
                lastLineNumber = ln
                val output = new CompositeOutput(blocks.map(_.format(logRecord)).toList)
                previousOutput = Some(output)
                output
            }
        }
    }
}
