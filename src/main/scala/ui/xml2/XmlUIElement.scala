package ui.xml2
import core.xml.XmlElement
import scala.util.Try
import ui.controls2.UIElement

object XmlUIElement {
    def fromFile(filePath:String):Try[UIElement] = {
        XmlElement.fromFile(filePath).flatMap(fromXmlElement);
    }

    def fromXmlElement(xmlElem:XmlElement):Try[UIElement] = {
        ???
    }
}