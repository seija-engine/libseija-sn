package core
import scala.collection.mutable;

trait IFromString[T <: IStringObject] {
    def setProperty(target:T,name:String,value:String) = {
        println(s"set ${name} ${value}")
    }
    
    def create():T;
}

trait IStringObject {
    val formStringTrait:IFromString[this.type]
}


object StringObject {
    private[this] var mapCreator = mutable.HashMap[String,IFromString[_]]()
    def create(name:String):Option[IStringObject] = {
        mapCreator.get(name).map(v => v.create())
    }
}