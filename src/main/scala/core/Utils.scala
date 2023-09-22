package core
import scala.util.Try

extension [T](v:Try[T]) {
   inline def logError():Try[T] = {
       v.failed.foreach(v => slog.error(v))
       v
   }
}