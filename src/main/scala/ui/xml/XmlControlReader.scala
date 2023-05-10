package ui.xml
import scala.collection.mutable
import core.xml.{XmlReader,XmlEvent}
import ui.BaseControl
import scala.util.Try
import scala.util.Failure
import scala.util.Success
import ui.binding.BindingItem

case class XmlControlReader(val reader:XmlReader,val templateOwner:Option[BaseControl]) {

    def read():Try[BaseControl] =  this.readControl()

    def readControl():Try[BaseControl] = Try {
        val readEvent = reader.nextEvent().get;
        readEvent match {
            case XmlEvent.EmptyElement(name) => {
                val pair = XmlControl.tryCreate(name).get;
                readStringProperty(reader,pair);
                return Success(pair.control);
            }
            case XmlEvent.StartElement(name) => {
                val pair = XmlControl.tryCreate(name).get;
                val curControlName = pair.setter.name;
                readStringProperty(reader,pair);
                
                while(true) {
                    val nextEvent = reader.lookNext().get;
                    if(nextEvent.IsEnd(pair.setter.name) || nextEvent.IsEOF()) {
                        reader.nextEvent();
                        return Success(pair.control)
                    }
                    //Start
                    val startEvent = nextEvent.castStart();
                    if(startEvent.isDefined) {
                        if(startEvent.get.startsWith(curControlName + ".")) {
                            readXmlProperty(pair).get;
                        } else {
                           val childControl = XmlControlReader(reader,templateOwner).read().get;
                           pair.control.AddChild(childControl,false)
                        }
                    }
                    //Empty
                    val emptyEvent = nextEvent.castEmpty();
                    if(emptyEvent.isDefined) {
                        val childControl = XmlControlReader(reader,templateOwner).read().get;
                        pair.control.AddChild(childControl,false)
                    }
                }
                pair.control
            }
            
            case ev => throw Throwable(s"error event ${ev.toString()}")
        }
    }

    def readXmlProperty(pair:FromXmlValuePair):Try[Unit] = {
        pair.setter.readXmlProperty(pair.control,this.reader)
    }

    private def readStringProperty(reader:XmlReader,control:FromXmlValuePair) = {
       var curAttr = reader.nextAttr();
       while(curAttr.isDefined) {
         val k = curAttr.get._1;
         val v = curAttr.get._2;
         if(v.startsWith("{Binding")) {
           BindingItem.parse(k,v) match {
             case Success(bindingItem) => {
                control.control.addBindItem(bindingItem)
             };
             case Failure(e) => System.err.println(s"parse binding error:${e.getMessage}");
           }
         } else {
            val curControl = control.control;
            control.setter.setStringPropery(curControl,k,v);
         }
         curAttr = reader.nextAttr();
       }
    }

  
}
