package ui.controls
import ui.controls.ControlTemplate
import core.reflect.*;
import core.logError;
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import scala.collection.mutable;
import ui.ElementNameScope
import scala.collection.mutable.HashMap

class Control extends UIElement with ElementNameScope derives ReflectType {
    var template:Option[ControlTemplate] = None
    var nameDict:HashMap[String,UIElement] = HashMap.empty;
    override def Awake(): Unit = {
       if(this.template.isDefined) {
         this.template.get.Awake();
         val typInfo = Assembly.getTypeInfo(this);
         this.template.get.applyType(typInfo);
       }
       super.Awake();
    }

    override def OnEnter(): Unit = {
        this.createBaseEntity(true);
        this.loadControlTemplate();
    }

    override protected def onViewStateChanged(changeGroup: String, newState: String): Unit = {
      super.onViewStateChanged(changeGroup,newState);
      if(this.template.isDefined) {
        val visualGroup = this.template.get.visualStateGroups.getGroup(changeGroup);
        if(visualGroup.isEmpty) return;
        this.applyVisualGroup(visualGroup.get,newState,Some(this));
      }
    }

    protected def loadControlTemplate(): Unit = {
       this.findTemplate().flatMap( t => t.LoadContent(this,Some(this))).logError().foreach { element => 
         this.addChild(element);
       }
    }

    def findTemplate():Try[ControlTemplate] = {
       if(this.template.isDefined) {
         return Success(this.template.get);
       }
       Failure(new Throwable("control template not found"))
    }

    def getScopeElement(name:String):Option[UIElement] = { this.nameDict.get(name) }

    override def setScopeElement(name: String, elem: UIElement): Unit = {
      this.nameDict.put(name,elem);
    }
}