package ui.xml
import core.xml;
import ui.BaseControl
import core.xml.XmlReader
import core.xml.XmlEvent
import scala.collection.mutable
import ui.controls.BaseLayout
import scala.util.Try
import scala.util.Failure
import scala.util.Success
import ui.binding.BindingItem
import core.reflect.TypeInfo
import core.reflect.Assembly
import core.xml.XmlElement

 
private case class FromXmlValuePair(control:BaseControl,info:TypeInfo)

object XmlControl {
    def create(name:String):Option[FromXmlValuePair] = {
        Assembly.get(name,true).map { info =>
           val newControl = info.create().asInstanceOf[BaseControl];
           FromXmlValuePair(newControl,info)
        }
    }

    def tryCreate(name:String):Try[FromXmlValuePair] = {
        create(name).toRight(Exception(s"not found control:$name")).toTry
    }

    def fromString(xmlString:String):Try[BaseControl] = {
        val reader = XmlReader.fromString(xmlString);
        val control = fromXmlReader(reader)
        reader.release();
        control
    }

    def fromXmlReader(reader:XmlReader):Try[BaseControl] = XmlRawControlReader(reader,None).read()
    
    def fromXmlElement(xmlElem:XmlElement):Try[BaseControl] = XmlElemControlReader(xmlElem,None).read()
}


