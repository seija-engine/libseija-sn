package slog

import slog.modify.LogModifier
import slog.handler.LogHandler
import java.io.PrintStream


case class Logger(parentId: Option[LoggerId] = Some(Logger.RootId),
                  modifiers: List[LogModifier] = Nil,
                  handlers: List[LogHandler] = Nil,
                  overrideClassName: Option[String] = None,
                  data: Map[String, () => Any] = Map.empty,
                  id: LoggerId = LoggerId()) {
    def includes(level: Level): Boolean = {
        false
    }

    def orphan(): Logger = copy(parentId = None)

    def setModifiers(modifiers: List[LogModifier]): Logger = copy(modifiers = modifiers.sorted)

    def clearModifiers(): Logger = setModifiers(Nil)

    def clearHandlers(): Logger = copy(handlers = Nil)

    def replace(name: Option[String] = None): Logger = name match {
        case Some(n) => Logger.replaceByName(n, this)
        case None => Logger.replace(this)
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

    def root: Logger = apply(RootId)

    def get(id: LoggerId): Option[Logger] = id2Logger.get(id)

    def apply(id: LoggerId): Logger = get(id) match {
        case Some(logger) => logger
        case None => {
            val logger = new Logger(id = id)
            id2Logger += logger.id -> logger
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
        root.orphan().clearModifiers().clearHandlers().replace(Some("root"))
    }
}