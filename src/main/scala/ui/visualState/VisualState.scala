package ui.visualState
import core.reflect.*;
import scala.collection.mutable.ArrayBuffer
import ui.resources.Setter
import scala.collection.mutable.Growable;
import ui.ContentProperty;
import ui.ElementNameScope

@ContentProperty("Setters")
class VisualState derives ReflectType {
    var name:String = "";
    var Setters:SettersGroup = SettersGroup()

    def applyType(info: Option[TypeInfo]):Unit = {
        this.Setters.setters.foreach(_.applyType(info))
    }

    def applyNameScope(nameScope:ElementNameScope):Unit = {
        this.Setters.setters.foreach(_.applyNameScope(nameScope))
    }
}

class SettersGroup extends Growable[Setter] derives ReflectType {
    var setters:ArrayBuffer[Setter] = ArrayBuffer[Setter]()
    def addOne(setter: Setter): this.type = {
        setters += setter;
        this
    }

    def clear(): Unit = {
        setters.clear()
    }
}