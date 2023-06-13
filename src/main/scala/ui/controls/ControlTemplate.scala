package ui.controls
import ui.ElementNameScope;
import ui.controls.BaseTemplate
import core.reflect.*;
import ui.ContentProperty
import scala.collection.mutable.ArrayBuffer
import scala.util.Try
import scala.collection.mutable.HashMap;
import core.ICopy;
import core.copyObject
import scala.util.Success
import ui.visualState.VisualStateGroupList
import ui.resources.IApplyStyleType
import ui.IAwake

@ContentProperty("content")
class ControlTemplate extends BaseTemplate with IApplyStyleType with ElementNameScope with IAwake derives ReflectType {
    var nameDict:HashMap[String,UIElement] = HashMap.empty;

    var content:UIElement = UIElement.zero;
    var visualStateGroups:VisualStateGroupList = VisualStateGroupList();
    override def Awake(): Unit = {
       this.putNameToScope(content);
    }

    protected def putNameToScope(element:UIElement):Unit = {
        if(element.Name != null) {
            this.nameDict.put(element.Name,element);
        }
        element.children.foreach(putNameToScope)
    }

    override def getScopeElement(name:String):Option[UIElement] = { this.nameDict.get(name) }

    

    override def LoadContent(parent:UIElement,nameScope:Option[ElementNameScope]): Try[UIElement] = {
        val instObject:UIElement = content.clone();
        setUIElementTemplate(instObject,parent,nameScope);
        Success(instObject)
    }

    override def applyType(info: Option[TypeInfo]): Unit = {
        this.visualStateGroups.applyType(info);
        this.visualStateGroups.applyNameScope(this);
    }
}