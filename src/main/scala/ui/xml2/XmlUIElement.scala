package ui.xml2
import core.xml.XmlElement
import scala.util.Try
import ui.controls2.UIElement
import core.reflect.Assembly
import core.reflect.TypeInfo
import scala.util.Success
import core.logError
import core.reflect.DynTypeConv
import ui.binding.BindingItem

object XmlUIElement {
    def fromFile(filePath:String):Try[UIElement] = {
        XmlElement.fromFile(filePath).flatMap(fromXmlElement);
    }

    def fromXmlElement(xmlElem:XmlElement):Try[UIElement] = Try {
        val typInfo = Assembly.getOrThrow(xmlElem.name,true);
        val newUIElement = typInfo.create().asInstanceOf[UIElement];
        setElemetStringProps(typInfo,newUIElement,xmlElem);
        if(xmlElem.children.length > 0) {
            newUIElement.handleXMLContent(xmlElem.children);
        }
        newUIElement
    }

    protected def setElemetStringProps(typInfo:TypeInfo,ui:UIElement,xml:XmlElement) = {
        for((k,v) <- xml.attributes) {
          if (v.startsWith("{Binding")) {
            BindingItem.parse(k, v).logError();
          } else {
            typInfo.getFieldTry(k).logError().foreach {field =>
                DynTypeConv.strConvertToTry(field.typName,v).logError().foreach { value =>
                    field.set(ui,value)
                }
            }
          }
        }
    }
}