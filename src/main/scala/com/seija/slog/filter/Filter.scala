package com.seija.slog.filter

import com.seija.slog.LogRecord

trait Filter {
  def matches(record: LogRecord): Boolean

  def &&(that: Filter): Filter = AndFilters(List(this, that))
  def ||(that: Filter): Filter = OrFilters(List(this, that))
}