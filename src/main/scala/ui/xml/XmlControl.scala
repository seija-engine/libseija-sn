package ui.xml
import core.xml;
import ui.BaseControl
import core.xml.XmlReader
import core.xml.XmlEvent
import scala.collection.mutable
import ui.controls.BaseLayout

trait IControlFromXml[T <: BaseControl] {
    val name:String;
    def create():T;
    def setStringPropery(control:T,name:String,value:String):Unit;
}

case class FromXmlValuePair(control:BaseControl,setter:IControlFromXml[BaseControl])

object XmlControl {
    private val _controlCreator = new mutable.HashMap[String,IControlFromXml[_]]();
    protected var readStack:mutable.Stack[FromXmlValuePair] = new mutable.Stack[FromXmlValuePair]();

    def register[T <: BaseControl]()(using v:IControlFromXml[T]) = {
        _controlCreator.put(v.name,v);
    }

    def create(name:String):Option[FromXmlValuePair] = {
        _controlCreator.get(name).map(x => {
            val control = x.create();
            FromXmlValuePair(control,x.asInstanceOf[IControlFromXml[BaseControl]])
        })
    }

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
                        XmlControl.create(name) match {
                            case Some(value) =>
                                 if(!this.readStack.isEmpty) {
                                    val lastControlName = this.readStack.top.setter.name;
                                    if(name.startsWith(lastControlName + ".")) {
                                        println(s"add params $name")
                                    }
                                 } else {
                                    readStringProperty(reader,value)
                                    this.readStack.push(value)
                                 }
                                
                            case None => println(s"not found $name")
                        }
                    } 
                    case XmlEvent.EndElement(name) => {
                        this.readStack.pop();
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

    private def readStringProperty(reader:XmlReader,control:FromXmlValuePair) = {
       var curAttr = reader.nextAttr();
       while(curAttr.isDefined) {
         val k = curAttr.get._1;
         val v = curAttr.get._2;
         val curControl = control.control;
         control.setter.setStringPropery(curControl,k,v);
         curAttr = reader.nextAttr();
       }
    }
}


