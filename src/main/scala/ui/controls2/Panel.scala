package ui.controls2
import core.reflect.*;
import scala.collection.mutable.ListBuffer
import core.xml.XmlElement
import ui.xml2.XmlUIElement
import core.logError;

class Panel extends UIElement derives ReflectType {

    override def OnEnter(): Unit = {
      this.createBaseEntity(true);
      //println(s"Panel OnEnter ${this.getEntity()} ${this.parent}")
    }

    
}