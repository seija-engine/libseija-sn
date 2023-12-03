package com.seija.slog

import com.seija.slog.modify.LogModifier
import com.seija.slog.handler.LogHandler
import java.io.PrintStream
import com.seija.slog.format.Formatter
import com.seija.slog.writer.Writer
import com.seija.slog.writer.ConsoleWriter
import com.seija.slog.output.format.OutputFormat
import com.seija.slog.handler.SynchronousLogHandle
import com.seija.slog.handler.LogHandle
import com.seija.slog.modify.LevelFilter


case class Logger(parentId: Option[LoggerId] = Some(Logger.RootId),
                  modifiers: List[LogModifier] = Nil,
                  handlers: List[LogHandler] = Nil,
                  overrideClassName: Option[String] = None,
                  data: Map[String, () => Any] = Map.empty,
                  id: LoggerId = LoggerId()) extends LoggerSupport[Unit] {
    
    private var lastUpdate = Logger.lastChange
    private var includeStatus = Map.empty[Level, Boolean]
    
    def includes(level: Level): Boolean = {
        if (lastUpdate != Logger.lastChange) {
            includeStatus = Map.empty
            lastUpdate = Logger.lastChange
        }
        includeStatus.get(level) match {
            case Some(b) => b
            case None =>
                val b = shouldLog(LogRecord.simple("", "", "", level = level))
                includeStatus += level -> b
                b
        }
    }

    protected def shouldLog(record: LogRecord): Boolean = record.modify(modifiers) match {
        case Some(_) if handlers.nonEmpty => true
        case Some(r) => parentId.map(Logger.apply).exists(p => p.shouldLog(r))
        case None => false
    }

    def orphan(): Logger = copy(parentId = None)

    def setModifiers(modifiers: List[LogModifier]): Logger = copy(modifiers = modifiers.sorted)

    def clearModifiers(): Logger = setModifiers(Nil)

    final def withModifier(modifier: LogModifier): Logger = setModifiers(modifiers.filterNot(m => m.id.nonEmpty && m.id == modifier.id) ::: List(modifier))

    def withMinimumLevel(level: Level): Logger = withModifier(LevelFilter >= level)

    def clearHandlers(): Logger = copy(handlers = Nil)

    def replace(name: Option[String] = None): Logger = name match {
        case Some(n) => Logger.replaceByName(n, this)
        case None => Logger.replace(this)
    }

    def withHandler(handler: LogHandler): Logger = copy(handlers = handlers ::: List(handler))
    def withHandler(formatter: Formatter = Formatter.default,
                  writer: Writer = ConsoleWriter,
                  minimumLevel: Option[Level] = None,
                  modifiers: List[LogModifier] = Nil,
                  outputFormat: OutputFormat = OutputFormat.default,
                  handle: LogHandle = SynchronousLogHandle): Logger = {
        withHandler(LogHandler(formatter, writer, minimumLevel, modifiers, outputFormat, handle))
    }

    override def log(record: LogRecord): Unit = {
        record.modify(modifiers).foreach { r =>
            handlers.foreach(_.log(r))
            parentId.map(Logger.apply).foreach(_.log(r))
        }
    }

    override def log(level: Level, features: LogFeature*)
                  (implicit pkg: sourcecode.Pkg, fileName: sourcecode.FileName, name: sourcecode.Name, line: sourcecode.Line): Unit = {
    if (includes(level)) { super.log(level, features: _*) }
  }
    
}

object Logger {
    private val systemOut = System.out
    private val systemErr = System.err

    object system {
        def out: PrintStream = systemOut
        def err: PrintStream = systemErr
    }

    private var lastChange: Long = 0L
    val RootId: LoggerId = LoggerId(0L)
    private var id2Logger: Map[LoggerId, Logger] = Map.empty
    private var name2Id: Map[String, LoggerId] = Map.empty

    resetRoot()

    def root: Logger = apply(RootId)

    def get(id: LoggerId): Option[Logger] = id2Logger.get(id)

    def get(name: String): Option[Logger] = name2Id.get(fixName(name)).flatMap(id => id2Logger.get(id))

    def apply(id: LoggerId): Logger = get(id) match {
        case Some(logger) => logger
        case None => {
            val logger = new Logger(id = id)
            id2Logger += logger.id -> logger
            lastChange = System.currentTimeMillis()
            logger
        }
    }

    def apply(name: String): Logger = get(name) match {
        case Some(logger) => logger
        case None => {
            val n = fixName(name)
            val dotIndex = n.lastIndexOf('.')
            val parentId = if (dotIndex > 0) {
                val parentName = n.substring(0, dotIndex)
                val parent = apply(parentName)
                parent.id
            } else {
                RootId
            }
            val logger = Logger(parentId = Some(parentId))
            id2Logger += logger.id -> logger
            name2Id += n -> logger.id
            lastChange = System.currentTimeMillis()
            logger
        }
    }

    

    def replace(logger: Logger): Logger = {
        id2Logger += logger.id -> logger
        lastChange = System.currentTimeMillis()
        logger
    }
    def replaceByName(name: String, logger: Logger): Logger = {
        replace(logger)
        name2Id += fixName(name) -> logger.id
        logger
    }

    private def fixName(name: String): String = name.replace("$", "")

    def resetRoot(): Unit = {
        root.orphan().clearModifiers().clearHandlers().withMinimumLevel(Level.Debug).withHandler().replace(Some("root"))
    }
}