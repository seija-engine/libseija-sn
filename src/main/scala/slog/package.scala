
import sourcecode.FileName

import sourcecode.Line

import sourcecode.Name

import sourcecode.Pkg
package object slog extends LoggerSupport[Unit] {
  
  inline override final def log(record: LogRecord): Unit = {
    Logger(record.className).log(record)
  }

  override def log(level: Level, features: LogFeature*)(implicit pkg: Pkg, fileName: FileName, name: Name, line: Line): Unit = {
    if (includes(level)) super.log(level, features: _*)
  }

  def includes(level: Level)(implicit pkg: sourcecode.Pkg,
                             fileName: sourcecode.FileName,
                             name: sourcecode.Name,
                             line: sourcecode.Line): Boolean = {
    val (_, className) = LoggerSupport.className(pkg, fileName)
    Logger(className).includes(level)
  }
}
