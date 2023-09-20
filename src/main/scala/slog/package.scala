package object slog extends LoggerSupport[Unit] {
  @inline
  override final def log(record: LogRecord): Unit = {
    println(record)
  }

  def includes(level: Level)(implicit pkg: sourcecode.Pkg,
                             fileName: sourcecode.FileName,
                             name: sourcecode.Name,
                             line: sourcecode.Line): Boolean = {
    val (_, className) = LoggerSupport.className(pkg, fileName)
    true
  }
}
