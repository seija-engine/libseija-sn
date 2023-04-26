package ui.xml
import core.xml;
import ui.BaseControl
import core.xml.XmlReader
import core.xml.XmlEvent
import core.StringObject
import core.{IFromString,IStringObject}

object XmlControl {
    def fromString(xmlString:String):BaseControl = {
        fromXmlReader(XmlReader.fromString(xmlString))
    }

    def fromXmlReader(reader:XmlReader):BaseControl = {
        var isRun = true;
        while(isRun) {
            reader.nextEvent() match {
                case Left(value) => println(value)
                case Right(value) => value match {
                    case XmlEvent.StartElement(name) => {
                       StringObject.create(name) match {
                           case Some(control) => setXmlTagProperty(reader,control);
                           case None => println(s"not found ${name}")
                        }
                    } 
                    case XmlEvent.EndElement(name) => {

                    } 
                    case XmlEvent.EmptyElement(name) => {

                    } 
                    case XmlEvent.Text(text) => 
                    case XmlEvent.Comment(text) => 
                    case _ => isRun = false
                }
            }
        }
        null
    }

    private def setXmlTagProperty(reader:XmlReader,control:IStringObject) = {
       var curAttr = reader.nextAttr();
       while(curAttr.isDefined) {
         val k = curAttr.get._1;
         val v = curAttr.get._2;
       }
    }
}
