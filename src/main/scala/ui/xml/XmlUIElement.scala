package ui.xml
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
import sxml.vm.{VMValue, XmlNode}

object XmlUIElement {
    def fromFile(filePath:String):Try[UIElement] = {
      val xmlElement = UISXmlEnv.evalFile(filePath).get.unwrap[VMValue.VMXml]().get.value
      fromXmlElement(xmlElement)
    }

    def fromXmlElement(xmlElem:XmlNode):Try[UIElement] = {
      val parser = SXmlObjectParser(XmlNSResolver.default);
      val curObject = parser.parse(xmlElem);
      val tryUIElement = curObject.map(_.asInstanceOf[UIElement])
      tryUIElement.map(_.Awake());
      tryUIElement
    }
}

case class NotFoundNSAlias(name:String) extends Exception;