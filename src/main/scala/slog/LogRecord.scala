package slog
import slog.LoggableMessage
import slog.format.FormatBlock
import slog.format.FormatBlock.NewLine
import slog.output.LogOutput
import slog.output.CompositeOutput
import slog.modify.LogModifier
import scala.annotation.tailrec

case class LogRecord(level: Level,
                     levelValue: Double,
                     messages: List[LoggableMessage],
                     fileName: String,
                     className: String,
                     methodName: Option[String],
                     line: Option[Int],
                     column: Option[Int],
                     timeStamp: Long = Time()) {

    protected var appliedModifierIds = Set.empty[String]

    val id: Long = LogRecord.incIdAndGet
    
    def withMessages(messages: LoggableMessage*): LogRecord = copy(messages = this.messages ::: messages.toList)

    lazy val logOutput: LogOutput = generateLogOutput()

    protected def generateLogOutput(): LogOutput = messages match {
        case msg :: Nil => msg.logOutput
        case list => new CompositeOutput(
            list.flatMap { message =>
                List(LogRecord.messageSeparator.format(this), message.logOutput)
            }.drop(1))
    }

    def checkModifierId(id: String, add: Boolean = true): Boolean = id match {
        case "" => false
        case _ if appliedModifierIds.contains(id) => true
        case _ =>
            if (add) appliedModifierIds += id
            false
    }

    def modify(modifier: LogModifier): Option[LogRecord] = if (checkModifierId(modifier.id)) {
        Some(this)
    } else {
        modifier(this)
    }
    
    @tailrec
    final def modify(modifiers: List[LogModifier]): Option[LogRecord] = if (modifiers.isEmpty) {
        Some(this)
    } else {
        modify(modifiers.head) match {
            case None => None
            case Some(record) => record.modify(modifiers.tail)
        }
    }
}

object LogRecord {
    private var inc:Long = 0L

    def incIdAndGet:Long = {
        inc = inc + 1
        inc
    }

    var messageSeparator: FormatBlock = NewLine


    def simple(message: String,
             fileName: String,
             className: String,
             methodName: Option[String] = None,
             line: Option[Int] = None,
             column: Option[Int] = None,
             level: Level = Level.Info,
             thread: Thread = Thread.currentThread(),
             timeStamp: Long = Time()): LogRecord = {
    apply(
      level = level,
      levelValue = level.value,
      messages = List(message),
      fileName = fileName,
      className = className,
      methodName = methodName,
      line = line,
      column = column,
      timeStamp = timeStamp
    )
  }
}