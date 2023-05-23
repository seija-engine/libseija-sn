package ui
import _root_.core.xml.XmlElement
import _root_.core.reflect.*
import scala.collection.mutable.ListBuffer
import ui.xml.XmlElemTemplateReader

class Template {
    var children:ListBuffer[BaseControl] = ListBuffer()
    def applyTo(parent:BaseControl) = {
        for(control <- this.children) {
            parent.AddChild(control.clone())
        }
    }
}


object Template {
    given Into[XmlElement,Template] with {
      override def into(valueXml: XmlElement): Template = {
        XmlElemTemplateReader(valueXml).read().get
      }
    }

    given Into[XmlElement,Option[Template]] with {
      override def into(fromValue: XmlElement): Option[Template] = Some(given_Into_XmlElement_Template.into(fromValue)) 
    }

}
