package core
import scala.collection.mutable;

trait IStringPropObject[T] {
    def setProperty(target:T,name:String,value:String):Unit;
    def create():T;
}

trait IFromString[T] {
    def from(strValue:String):Option[T];
}

def formString[T](str:String)(using v:IFromString[T]):Option[T] = v.from(str)

case class ObjectPair[T](obj:T,setter:IStringPropObject[T])

object StringObject {

    private[this] var mapCreator = mutable.HashMap[String,IStringPropObject[_]]()
    def register(name:String,creator:IStringPropObject[_]) = mapCreator.put(name,creator)

    def create(name:String):Option[ObjectPair[_]] = {
        mapCreator.get(name).map(v => ObjectPair(v.create(),v))
    }
}

given IntFormString:IFromString[Int] with {
  override def from(strValue: String): Option[Int] = strValue.toIntOption
}