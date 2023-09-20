package slog
import slog.LoggableMessage


case class LogRecord(level: Level,
                     levelValue: Double,
                     messages: List[LoggableMessage],
                     fileName: String,
                     className: String,
                     methodName: Option[String],
                     line: Option[Int],
                     column: Option[Int],
                     timeStamp: Long = Time()) {
    
    final val id: Long =  {
        LogRecord.incrementor += 1
        LogRecord.incrementor
    }
    
    def withMessages(messages: LoggableMessage*): LogRecord = copy(messages = this.messages ::: messages.toList)
}

object LogRecord {
    private var incrementor = 0L
}