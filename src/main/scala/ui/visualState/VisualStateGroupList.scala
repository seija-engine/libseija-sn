package ui.visualState
import scala.collection.mutable.{Growable,ArrayBuffer,HashMap};
import core.reflect.*;
import ui.ContentProperty;
import ui.resources.IApplyStyleType
import ui.ElementNameScope

@ContentProperty("content")
class VisualStateGroupList extends IApplyStyleType derives ReflectType {
    var content:VisualStateGroupInnerList = VisualStateGroupInnerList()

    def isEmpty():Boolean = this.content.groupDict.size == 0

    def getGroup(name:String):Option[VisualStateGroup] = {
        this.content.groupDict.get(name)
    }
    
    override def applyType(info: Option[TypeInfo]): Unit = {
        this.content.applyType(info);
    }

    def applyNameScope(nameScope:ElementNameScope):Unit = {
        this.content.applyNameScope(nameScope);
    }

    override def clone():VisualStateGroupList = {
        super.clone().asInstanceOf[VisualStateGroupList];
    }
}

class VisualStateGroupInnerList extends Growable[VisualStateGroup] derives ReflectType {
    var groupDict:HashMap[String,VisualStateGroup] = HashMap.empty;
    def addOne(group: VisualStateGroup): this.type = {
        this.groupDict.update(group.name,group);
        this
    }
    
    def applyType(info: Option[TypeInfo]): Unit = {
        this.groupDict.values.foreach(_.applyType(info));
    }

    def applyNameScope(nameScope:ElementNameScope):Unit = {
        this.groupDict.values.foreach(_.applyNameScope(nameScope))
    }

    def clear(): Unit = {
        this.groupDict.clear();
    }
}