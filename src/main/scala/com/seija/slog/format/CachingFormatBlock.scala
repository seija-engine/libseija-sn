package com.seija.slog.format

import com.seija.slog.output.LogOutput
import com.seija.slog.LogRecord

trait CachingFormatBlock extends FormatBlock {
 
  protected def cacheLength: Long

  private var cache:Option[LogOutput] = None
  private var lastTimeStamp = 0L

  override final def format(record: LogRecord): LogOutput = {
    val timeStamp = record.timeStamp
    if (timeStamp - lastTimeStamp > cacheLength) {
      val output = formatCached(record)
      cache = Some(output)
      lastTimeStamp = timeStamp
      output
    } else {
      cache.get
    }
  }

  protected def formatCached(record: LogRecord): LogOutput
}