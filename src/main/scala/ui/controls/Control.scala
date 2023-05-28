package ui.controls
import ui.controls.ControlTemplate
import core.reflect.*;
import core.logError;
class Control extends UIElement derives ReflectType {
    var template:Option[ControlTemplate] = None

    override def Enter(): Unit = {
        if(this.template.isDefined) {
          this.template.get.LoadContent().logError().foreach {element =>
             element.setParent(this.parent);
             element.Enter();
             this.entity = element.getEntity();    
          }
        }
        super.Enter();
    }

   
}