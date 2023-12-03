package com.seija.core
import scala.util.Try
import com.seija.slog
extension [T](v:Try[T]) {
   inline def logError():Try[T] = {
       //v.failed.foreach(v => com.seija.slog.error(v))
       v
   }
}