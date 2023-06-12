package ui.xml
import core.xml.XmlElement
import scala.util.Try
import ui.controls.UIElement
import core.reflect.Assembly
import core.reflect.{TypeInfo,FieldInfo}
import scala.util.Success
import core.logError
import ui.ContentProperty;
import core.reflect.DynTypeConv
import ui.binding.BindingItem
import ui.ContentProperty
import scala.util.Failure
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Buffer

object XmlUIElement {
    def fromFile(filePath:String):Try[UIElement] = {
      val xmlElement = XmlElement.fromFile(filePath).get;
      fromXmlElement(xmlElement)
    }

    def fromXmlElement(xmlElem:XmlElement):Try[UIElement] = {
      val curObject = XmlObjectParser(XmlNSResolver.default).parse(xmlElem);
      val tryUIElement = curObject.map(_.asInstanceOf[UIElement])
      tryUIElement.map(_.Awake());
      tryUIElement
    }
}

case class NotFoundNSAlias(name:String) extends Exception;