package ui.controls
import ui.controls.ControlTemplate
import core.reflect.*;
import core.logError;
import scala.util.Try
import scala.util.Success
import scala.util.Failure

class Control extends UIElement derives ReflectType {
    var template:Option[ControlTemplate] = None


    override def OnEnter(): Unit = {
        this.createBaseEntity(true);
        this.findTemplate().logError().foreach { tem =>
            tem.LoadContent(this).logError().foreach {element =>
                element.setParent(Some(this));
                element.Enter(); 
            }
        }
    }

    def findTemplate():Try[ControlTemplate] = {
       if(this.template.isDefined) {
         return Success(this.template.get);
       }
       Failure(new Throwable("template not found"))
    }
}