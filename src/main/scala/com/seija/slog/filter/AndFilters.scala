package com.seija.slog.filter

import com.seija.slog.LogRecord

case class AndFilters(filters: List[Filter]) extends Filter {
  override def matches(record: LogRecord): Boolean = filters.forall(_.matches(record))

  override def &&(that: Filter): Filter = copy(filters ::: List(that))
}