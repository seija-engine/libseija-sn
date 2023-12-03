package com.seija.slog.modify

import com.seija.slog.{LogRecord,Priority}

trait LogModifier {
 
  def id: String

  
  def priority: Priority

  
  def apply(record: LogRecord): Option[LogRecord]

  def withId(id: String): LogModifier

  def alwaysApply: LogModifier = withId("")
}

object LogModifier {
  implicit final val LogModifierOrdering: Ordering[LogModifier] = Ordering.by(_.priority)
}