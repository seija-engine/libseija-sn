package com.seija.slog

case class Level(name: String, value: Double) {
  def namePadded: String = Level.padded(this)

  def test() = {
  }
}


object Level {
  implicit final val LevelOrdering: Ordering[Level] = Ordering.by[Level, Double](_.value).reverse

  val Trace: Level = Level("TRACE", 100.0)
  val Debug: Level = Level("DEBUG", 200.0)
  val Info: Level = Level("INFO", 300.0)
  val Warn: Level = Level("WARN", 400.0)
  val Error: Level = Level("ERROR", 500.0)
  val Fatal: Level = Level("FATAL", 600.0)

  private var padded = Map.empty[Level, String]
}