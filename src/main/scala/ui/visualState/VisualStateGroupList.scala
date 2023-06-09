package ui.visualState
import scala.collection.mutable.{Growable,ArrayBuffer};
import core.reflect.*;

class VisualStateGroupList extends Growable[VisualStateGroup] derives ReflectType {
    def addOne(state: VisualStateGroup): this.type = {
        
        this 
    }
    def clear(): Unit = {
    }
}