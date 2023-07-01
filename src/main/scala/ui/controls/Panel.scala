package ui.controls
import core.reflect.*;
import scala.collection.mutable.ListBuffer
import core.xml.XmlElement
import ui.xml.XmlUIElement
import core.logError;
import ui.ContentProperty
import ui.core.Canvas;
import ui.xml.IXmlObject

@ContentProperty("children")
class Panel extends UIElement with IXmlObject derives ReflectType {
    var isClip:Boolean = false;
    var isCanvas:Boolean = false;
    override def OnEnter(): Unit = {
      this.createBaseEntity(true);
      this.checkAddCanvas();
      //println(s"Panel OnEnter ${this.getEntity()} ${this.parent}")
    }

    def checkAddCanvas():Unit = {
      if(this.isCanvas || this.isClip) {
         val curEntity = this.getEntity().get;
         curEntity.add[Canvas](builder => {
            builder.isClip = this.isClip;
         });
      }
    }

    override def OnAddContent(value: Any): Unit = {
      if(value.isInstanceOf[UIElement]) {
        value.asInstanceOf[UIElement].setParent(Some(this));
      }
    }
    
}