package ui.controls2

import scala.collection.mutable.ListBuffer
import core.xml.XmlElement

class Panel extends UIElement {
    val Children:ListBuffer[UIElement] = ListBuffer[UIElement]();

    override def handleXMLContent(xmlElement: XmlElement): XmlElement = {
       var xmlElem = super.handleXMLContent(xmlElement);
       
       xmlElem 
    }
}