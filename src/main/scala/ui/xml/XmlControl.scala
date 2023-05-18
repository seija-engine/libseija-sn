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


case class FromXmlValuePair(control:BaseControl,info:TypeInfo)

object XmlControl {

   

    def create(name:String):Option[FromXmlValuePair] = {
        Assembly.get(name,true).map { info =>
           val newControl = info.create().asInstanceOf[BaseControl];
           FromXmlValuePair(newControl,info)
        }
    }

    def tryCreate(name:String):Try[FromXmlValuePair] = {
        create(name) match
            case None => Failure(Throwable(s"not found control:$name"))
            case Some(value) => Success(value)
        
    }

    def fromString(xmlString:String):Try[BaseControl] = {
        fromXmlReader(XmlReader.fromString(xmlString))
    }

    def fromXmlReader(reader:XmlReader):Try[BaseControl] = XmlControlReader(reader,None).read()
   
}


