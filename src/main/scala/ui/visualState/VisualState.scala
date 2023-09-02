package ui.visualState
import core.reflect.*;
import scala.collection.mutable.ArrayBuffer
import ui.resources.OldSetter
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

class SettersGroup extends Growable[OldSetter] derives ReflectType {
    var setters:ArrayBuffer[OldSetter] = ArrayBuffer[OldSetter]()
    def addOne(setter: OldSetter): this.type = {
        setters += setter;
        this
    }

    def clear(): Unit = {
        setters.clear()
    }
}