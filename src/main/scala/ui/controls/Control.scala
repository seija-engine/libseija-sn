package ui.controls
import ui.controls.ControlTemplate
import core.reflect.*;
import core.logError;
import scala.util.Try
import scala.util.Success
import scala.util.Failure

class Control extends UIElement derives ReflectType {
    var template:Option[ControlTemplate] = None
    
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

    protected def loadControlTemplate(): Unit = {
       this.findTemplate().flatMap( t => t.LoadContent(this)).logError().foreach { element => 
         this.addChild(element);
       }
    }

    def findTemplate():Try[ControlTemplate] = {
       if(this.template.isDefined) {
         return Success(this.template.get);
       }
       Failure(new Throwable("control template not found"))
    }
}