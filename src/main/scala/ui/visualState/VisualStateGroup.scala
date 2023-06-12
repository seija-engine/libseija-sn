package ui.visualState
import core.reflect.*;
import scala.collection.mutable.{Growable,ArrayBuffer};
import ui.resources.Setter
import ui.ContentProperty;
import ui.ElementNameScope
import scala.collection.mutable.HashMap

@ContentProperty("stateList")
class VisualStateGroup derives ReflectType {
    var name:String = "";
    var stateList:ArrayBuffer[VisualState] = ArrayBuffer[VisualState]()
    var stateDict:HashMap[String,VisualState] = null;

    def getState(name:String):Option[VisualState] = {
        if(stateDict == null) {
            this.stateDict = HashMap.empty;
            this.stateList.foreach { state => 
                this.stateDict.put(state.name,state);    
            };
        }
        this.stateDict.get(name)
    }

    def applyType(info: Option[TypeInfo]): Unit = {
        this.stateList.foreach(_.applyType(info));
    }

    def applyNameScope(nameScope:ElementNameScope):Unit = {
        this.stateList.foreach(_.applyNameScope(nameScope))
    }
}