package core
import scala.collection.mutable;

trait IFromString[T] {
    def from(strValue:String):Option[T];
}

def formString[T](str:String)(using v:IFromString[T]):Option[T] = v.from(str)

given IFromString[Boolean] with {
    def from(strValue:String):Option[Boolean] = {
        strValue match
            case "true" => Some(true)
            case "false" => Some(false)
            case _ => None
    }
}