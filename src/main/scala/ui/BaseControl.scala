package ui
import _root_.core.Entity;
import java.util.ArrayList

class BaseControl {
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

    def Enter():Unit = { 
        this.isEntered = true;
        this.OnEnter();
    }

    def OnEnter() = {}

    def Exit():Unit = {}
}