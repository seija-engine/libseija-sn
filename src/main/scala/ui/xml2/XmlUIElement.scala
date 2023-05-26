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
import ui.ContentProperty

object XmlUIElement {
    def fromFile(filePath:String):Try[UIElement] = {
        XmlElement.fromFile(filePath).flatMap(fromXmlElement(_,None));
    }

    def fromXmlElement(xmlElem:XmlElement,templateParent:Option[UIElement]):Try[UIElement] = Try {
        val typInfo:TypeInfo = Assembly.getTry(xmlElem.name,true).get;
        val newUIElement = typInfo.create().asInstanceOf[UIElement];
        setElemetStringProps(typInfo,newUIElement,xmlElem);

        /*
        Content:
        <Button>
          <Button.width>123</Button.width>
          <Button.template>
            <ControlTemplate>
              <ContentPresenter />
            </ControlTemplate>
          </Button.template>
          OK
        </Button>
        */
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
        newUIElement
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
}