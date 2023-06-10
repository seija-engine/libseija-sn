package ui.visualState
import core.reflect.*;
import scala.collection.mutable.ArrayBuffer
import ui.resources.Setter
import scala.collection.mutable.Growable;
import ui.ContentProperty;

@ContentProperty("Setters")
class VisualState derives ReflectType {
    var name:String = "";
    var Setters:SettersGroup = SettersGroup()

    def applyType(info: Option[TypeInfo]):Unit = {
        this.Setters.applyType(info);
    }
}

class SettersGroup extends Growable[Setter] derives ReflectType {
    var setters:ArrayBuffer[Setter] = ArrayBuffer[Setter]()
    def addOne(setter: Setter): this.type = {
        println(s"add ${setter}");
        setters += setter;
        this
    }

    def applyType(info: Option[TypeInfo]):Unit = {
        this.setters.foreach(_.applyType(info));
    }

    def clear(): Unit = {
        setters.clear()
    }
}