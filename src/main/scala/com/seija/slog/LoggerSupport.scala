package com.seija.slog

import com.seija.slog.{Level}

trait LoggerSupport[F] extends Any {
  def log(record: LogRecord): Unit

  def log(level: Level,features: LogFeature*)(implicit
      pkg: sourcecode.Pkg,
      fileName: sourcecode.FileName,
      name: sourcecode.Name,
      line: sourcecode.Line
  ): Unit = {
    val r = LoggerSupport(level, Nil, pkg, fileName, name, line)
    val record = features.foldLeft(r)((record, feature) => feature(record))
    log(record)
  }

  def trace(features: LogFeature*)(implicit
      pkg: sourcecode.Pkg,
      fileName: sourcecode.FileName,
      name: sourcecode.Name,
      line: sourcecode.Line
  ):Unit = {
    this.log(Level.Trace, features: _*)
  }

  def debug(features: LogFeature*)(implicit
      pkg: sourcecode.Pkg,
      fileName: sourcecode.FileName,
      name: sourcecode.Name,
      line: sourcecode.Line
  ):Unit = {
    this.log(Level.Debug, features: _*)
  }

  def info(features: LogFeature*)(implicit
      pkg: sourcecode.Pkg,
      fileName: sourcecode.FileName,
      name: sourcecode.Name,
      line: sourcecode.Line
  ):Unit = {
    this.log(Level.Info, features: _*)
  }

  def warn(features: LogFeature*)(implicit
      pkg: sourcecode.Pkg,
      fileName: sourcecode.FileName,
      name: sourcecode.Name,
      line: sourcecode.Line
  ):Unit = {
    this.log(Level.Warn, features: _*)
  }

  def error(features: LogFeature*)(implicit
      pkg: sourcecode.Pkg,
      fileName: sourcecode.FileName,
      name: sourcecode.Name,
      line: sourcecode.Line
  ):Unit = {
    this.log(Level.Error, features: _*)
  }
}

object LoggerSupport {
  private var map =
    Map.empty[sourcecode.Pkg, Map[sourcecode.FileName, (String, String)]]

  def apply(
      level: Level,
      messages: List[String],
      pkg: sourcecode.Pkg,
      fileName: sourcecode.FileName,
      name: sourcecode.Name,
      line: sourcecode.Line
  ): LogRecord = {
    val (fn, className) = LoggerSupport.className(pkg, fileName)
    val methodName = name.value match {
      case "anonymous" | "" => None
      case v => Option(v)
    }
    LogRecord(
      level = level,
      levelValue = level.value,
      Nil,
      fileName = fn,
      className = className,
      methodName = methodName,
      line = Some(line.value),
      column = None,
    )
  }

  private def generateClassName(
      pkg: sourcecode.Pkg,
      fileName: sourcecode.FileName
  ): (String, String) = {
    val backSlash = fileName.value.lastIndexOf('\\')
    val fn = if (backSlash != -1) {
      fileName.value.substring(backSlash + 1)
    } else {
      fileName.value
    }
    fn -> s"${pkg.value}.${fn.substring(0, fn.length - 6)}"
  }

  def className(
      pkg: sourcecode.Pkg,
      fileName: sourcecode.FileName
  ): (String, String) = map.get(pkg) match {
    case Some(m) =>
      m.get(fileName) match {
        case Some(value) => value
        case None =>
          val value = generateClassName(pkg, fileName)
          LoggerSupport.synchronized {
            map += pkg -> (m + (fileName -> value))
          }
          value
      }
    case None =>
      val value = generateClassName(pkg, fileName)
      LoggerSupport.synchronized {
        map += pkg -> Map(fileName -> value)
      }
      value
  }
}
