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

trait IControlFromXml[T <: BaseControl] {
    val name:String;
    def create():T;
    def setStringPropery(control:T,name:String,value:String):Unit;
    def readXmlProperty(control:T,reader:XmlReader):Try[Unit] = { Failure(NotImplementedError()) }
}

case class FromXmlValuePair(control:BaseControl,setter:IControlFromXml[BaseControl])

object XmlControl {
    private val _controlCreator = new mutable.HashMap[String,IControlFromXml[_]]();

    def register[T <: BaseControl]()(using v:IControlFromXml[T]) = {
        _controlCreator.put(v.name,v);
    }

    def create(name:String):Option[FromXmlValuePair] = {
        _controlCreator.get(name).map(x => {
            val control = x.create();
            FromXmlValuePair(control,x.asInstanceOf[IControlFromXml[BaseControl]])
        })
    }

    def tryCreate(name:String):Try[FromXmlValuePair] = {
        create(name) match
            case None => Failure(Throwable(s"not found control:$name"))
            case Some(value) => Success(value)
        
    }

    def fromString(xmlString:String):Try[BaseControl] = {
        fromXmlReader(XmlReader.fromString(xmlString))
    }

    def fromXmlReader(reader:XmlReader):Try[BaseControl] = XmlControlReader(reader).read()
   
}


