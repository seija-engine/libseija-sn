package ui.xml
import scala.collection.mutable
import core.xml.{XmlReader, XmlEvent}
import ui.BaseControl
import scala.util.Try
import scala.util.Failure
import scala.util.Success
import ui.binding.BindingItem
import scala.util.boundary, boundary.break
import core.reflect.Assembly
import core.reflect.DynTypeConv
import core.reflect.Assembly.nameOf

case class XmlControlReader(
    val reader: XmlReader,
    val templateOwner: Option[BaseControl]
) {

  def read(): Try[BaseControl] = this.readControl()

  def readControl(): Try[BaseControl] = Try {
    val readEvent = reader.nextEvent().get;
    readEvent match {
      case XmlEvent.EmptyElement(name) => {
        val pair = XmlControl.tryCreate(name).get;
        readStringProperty(reader, pair);
        pair.control
      }
      case XmlEvent.StartElement(name) => {
        val pair = XmlControl.tryCreate(name).get;
        val curControlName = pair.info.shortName;
        readStringProperty(reader, pair);

        while (true) {
          val nextEvent = reader.lookNext().get;
          if (nextEvent.IsEnd(pair.info.shortName) || nextEvent.IsEOF()) {
            reader.nextEvent();
            return Success(pair.control);
          }
          // Start
          val startEvent = nextEvent.castStart();
          if (startEvent.isDefined) {
            if (startEvent.get.startsWith(curControlName + ".")) {
              readXmlProperty(startEvent.get,pair).get;
            } else {
              val childControl =
                XmlControlReader(reader, templateOwner).read().get;
              pair.control.AddChild(childControl, false)
            }
          }
          // Empty
          val emptyEvent = nextEvent.castEmpty();
          if (emptyEvent.isDefined) {
            val childControl =
              XmlControlReader(reader, templateOwner).read().get;
            pair.control.AddChild(childControl, false)
          }
        }
        pair.control
      }

      case ev => throw Throwable(s"error event ${ev.toString()}")
    }
  }

  def readXmlProperty(startName:String,pair: FromXmlValuePair): Try[Unit] = {
    pair.control.readXmlProperty(startName,reader);
    Success(())
  }

  private def readStringProperty(
      reader: XmlReader,
      control: FromXmlValuePair
  ):Unit = {
    var curAttr = reader.nextAttr()
    if(curAttr.isFailure) {
       System.err.println(s"Xml readStringProperty error:${curAttr.failed.get.getMessage}");
       return;
    }
    while (curAttr.get.isDefined) {
      val k = curAttr.get.get._1;
      val v = curAttr.get.get._2;
      if (v.startsWith("{Binding")) {
        BindingItem.parse(k, v) match {
          case Success(bindingItem) => {
            control.control.addBindItem(bindingItem)
          };
          case Failure(e) =>
            System.err.println(s"parse binding error:${e.getMessage} k:$k v:$v");
        }
      } else {
        val curControl = control.control;
        setStringProperty(curControl, k, v) match {
            case Success(_) => {}
            case Failure(e) =>
                System.err.println(s"set property error:${e.getMessage} k:$k v:$v");
        }
      }
      curAttr = reader.nextAttr();
      if(curAttr.isFailure) {
        System.err.println(s"Xml readStringProperty error:${curAttr.failed.get.getMessage}");
        return;
      }
    }
  }

  private def setStringProperty(control:BaseControl,fieldName:String,value:String):Try[Unit] = {
    val typInfo = Assembly.getTypeInfo_?(control);
    val fieldType = typInfo.getField_?(fieldName);
    val convValue = DynTypeConv.strConvert(nameOf[String],fieldType.typName,value);
    convValue match
      case None => throw Exception(s"not found conv typ ${fieldType.typName} ${fieldType.Name}")
      case Some(value) => {
          fieldType.set(control,value.get)
      }
    Success(())
  }
}
