package com.seija.core
import scala.util.Try
import com.seija
extension [T](v:Try[T]) {
   inline def logError():Try[T] = {
       v.failed.foreach(v => slog.error(v))
       v
   }
}