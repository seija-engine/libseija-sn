package ui
import _root_.core.Entity;
import java.util.ArrayList
import _root_.core.IFromString

class BaseControl extends  INotifyPropertyChanged {
    protected var isEntered:Boolean = false;
    protected var entity:Option[Entity] = None;
    var Name: String = "";
    var ClassName: String = "";
    protected var parent:Option[BaseControl] = None;
    protected var childrenList: ArrayList[BaseControl] = new ArrayList[BaseControl]();
    protected var template:Option[Template] = None;

    def setParent(parent:Option[BaseControl]) = {
        this.parent = parent;
    }

    def getEntity():Option[Entity] = this.entity

    def AddChild(child:BaseControl) = {
        child.setParent(Some(this));
        child.Enter();
        this.childrenList.add(child);
    }

    override def onPropertyChanged(propertyName: String): Unit = {
        println(propertyName)
    }
    

    def Enter():Unit = { 
        this.isEntered = true;
        this.OnEnter();
    }

    def OnEnter() = {}

    def Exit():Unit = {}
}