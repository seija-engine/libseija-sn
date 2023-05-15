package core
import scala.collection.mutable;
trait IFromString[T] {
    def from(strValue:String):Option[T];
}

def formString[T](str:String)(using v:IFromString[T]):Option[T] = v.from(str)

object IFromString {
    given IFromString[Boolean] with {
        def from(strValue:String):Option[Boolean] = strValue.toBooleanOption
    }

    given IFromString[Int] with {
        def from(strValue:String):Option[Int] = strValue.toIntOption
    }
}

