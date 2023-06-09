package ui.visualState
import core.reflect.*;
import scala.collection.mutable.{Growable,ArrayBuffer};
import ui.resources.Setter

class VisualStateGroup extends Growable[VisualState] derives ReflectType {
    var groupList:ArrayBuffer[VisualState] = ArrayBuffer[VisualState]()

    def addOne(state: VisualState): this.type = {
        this.groupList += state;
        this 
    }
    def clear(): Unit = {
        this.groupList.clear()
    }
}