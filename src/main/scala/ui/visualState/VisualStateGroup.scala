package ui.visualState
import core.reflect.*;
import scala.collection.mutable.{Growable,ArrayBuffer};
import ui.resources.Setter
import ui.ContentProperty;
import ui.ElementNameScope

@ContentProperty("stateList")
class VisualStateGroup derives ReflectType {
    var name:String = "";
    var stateList:ArrayBuffer[VisualState] = ArrayBuffer[VisualState]()

    def applyType(info: Option[TypeInfo]): Unit = {
        this.stateList.foreach(_.applyType(info));
    }

    def applyNameScope(nameScope:ElementNameScope):Unit = {
        this.stateList.foreach(_.applyNameScope(nameScope))
    }
}