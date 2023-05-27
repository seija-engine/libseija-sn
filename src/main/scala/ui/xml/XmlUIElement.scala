package ui.xml
import core.xml.XmlElement
import scala.util.Try
import ui.controls.UIElement
import core.reflect.Assembly
import core.reflect.TypeInfo
import scala.util.Success
import core.logError
import core.reflect.DynTypeConv
import ui.binding.BindingItem
import ui.ContentProperty
import scala.util.Failure

object XmlUIElement {
    def fromFile(filePath:String):Try[UIElement] = {
        XmlElement.fromFile(filePath).flatMap(fromXmlElement(_,None));
    }

    def fromXmlElement(xmlElem:XmlElement,templateParent:Option[UIElement]):Try[UIElement] = Try {
      val typInfo = this.getType(xmlElem.name).get;
      val newUIElement = typInfo.create().asInstanceOf[UIElement];
      setElemetStringProps(typInfo,newUIElement,xmlElem);
      ???
      /*
        val typInfo:TypeInfo = Assembly.getTry(xmlElem.name).get;
        
        

        
        val contentAnn = typInfo.getAnnotation[ContentProperty]
        for(childElem <- xmlElem.children) {
          if(childElem.name.startsWith(s"${xmlElem.name}.")) {
            val propName = childElem.name.substring(xmlElem.name.length() + 1,childElem.name.length());
            typInfo.getFieldTry(propName).logError().foreach {field =>
                println(field)
            }
          } else {
            fromXmlElement(childElem,Some(newUIElement)).logError();
          }
        }
        newUIElement*/
    }

    protected def setElemetStringProps(typInfo:TypeInfo,ui:UIElement,xml:XmlElement) = {
        for((k,v) <- xml.attributes) {
          if (v.startsWith("{Binding")) {
            BindingItem.parse(k, v).logError().foreach(ui.addBindItem(_))
          } else {
            typInfo.getFieldTry(k).logError().foreach {field =>
                DynTypeConv.strConvertToTry(field.typName,v).logError().foreach { value =>
                    field.set(ui,value)
                }
            }
          }
        }
    }

    def getType(xmlName:String):Try[TypeInfo] = {
      val fullTypeName = XmlReadSetting.default.toFullName(xmlName);
      if(fullTypeName.isEmpty) {
        return Failure(NotFoundNSAlias(xmlName))
      }
      Assembly.getTry(fullTypeName.get)
    }
}

case class NotFoundNSAlias(name:String) extends Exception;