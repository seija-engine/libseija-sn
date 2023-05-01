package ui
import _root_.core.Entity;
import java.util.ArrayList
import _root_.core.IFromString

class BaseControl extends INotifyPropertyChanged with Cloneable {
    protected var isEntered:Boolean = false;
    protected var entity:Option[Entity] = None;
    var Name: String = "";
    var ClassName: String = "";
    protected var parent:Option[BaseControl] = None;
    protected var childrenList: ArrayList[BaseControl] = new ArrayList[BaseControl]();

    def setParent(parent:Option[BaseControl]) = {
        this.parent = parent;
    }

    def getEntity():Option[Entity] = this.entity

    def AddChild(child:BaseControl,isEnter:Boolean = true) = {
        child.setParent(Some(this));
        if(isEnter) {
           child.Enter();
        }
        this.childrenList.add(child);
    }

   
    override def onPropertyChanged(propertyName: String): Unit = {
              
    }
    

    def Enter():Unit = {
        if(!this.isEntered) {
          this.isEntered = true;
          this.OnEnter();
        }
        this.childrenList.forEach(_.Enter());
        
    }

    def OnEnter() = {}

    def Exit():Unit = {}
}