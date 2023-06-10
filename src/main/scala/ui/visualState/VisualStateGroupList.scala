package ui.visualState
import scala.collection.mutable.{Growable,ArrayBuffer,HashMap};
import core.reflect.*;
import ui.ContentProperty;
import ui.resources.IApplyStyleType

@ContentProperty("content")
class VisualStateGroupList extends IApplyStyleType derives ReflectType {
    var content:VisualStateGroupInnerList = VisualStateGroupInnerList()

    
    override def applyType(info: Option[TypeInfo]): Unit = {
        this.content.applyType(info);
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

    def clear(): Unit = {
        this.groupDict.clear();
    }
}