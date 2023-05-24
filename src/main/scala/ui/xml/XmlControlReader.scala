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
import core.xml.XmlElement
import scala.collection.mutable.Stack
import ui.Template

object XmlControlReader {
  def setControlStringProperty(control: BaseControl,key: String,value: String): Unit = {
    if (value.startsWith("{Binding")) {
      BindingItem.parse(key, value) match {
        case Success(bindingItem) => control.addBindItem(bindingItem)
        case Failure(e) =>
          System.err.println(s"parse binding error:${e.getMessage} k:$key v:$value");
      }
    } else {
      val fieldType = Assembly.getTypeInfo(control).flatMap(_.getField(key));
      if (fieldType.isEmpty) { System.err.println(s"not found field $key = $value"); return; }
      val convValue = DynTypeConv.strConvertTo(fieldType.get.typName, value);
      if (convValue.isEmpty) { System.err.println(s"not found conv typ $key:${fieldType.get.typName}"); return; }
      convValue.get match {
        case Failure(exception) =>
          System.err.println(
            s"conv error $key:${fieldType.get.typName} $value ${exception.getMessage}"
          )
        case Success(endValue) => fieldType.get.set(control, endValue)
      }
    }
  }
}

case class XmlRawControlReader(
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
              readXmlProperty(startEvent.get, pair).get;
            } else {
              val childControl =
                XmlRawControlReader(reader, templateOwner).read().get;
              pair.control.AddChild(childControl, false)
            }
          }
          // Empty
          val emptyEvent = nextEvent.castEmpty();
          if (emptyEvent.isDefined) {
            val childControl =
              XmlRawControlReader(reader, templateOwner).read().get;
            pair.control.AddChild(childControl, false)
          }
        }
        pair.control
      }

      case ev => throw Exception(s"error event ${ev.toString()}")
    }
  }

  def readXmlProperty(startName: String, pair: FromXmlValuePair): Try[Unit] = {
    pair.control.readXmlProperty(startName, reader);
    Success(())
  }

  private def readStringProperty(
      reader: XmlReader,
      control: FromXmlValuePair
  ): Unit = {
    var curAttr = reader.nextAttr()
    if (curAttr.isFailure) {
      System.err.println(s"Xml readStringProperty error:${curAttr.failed.get.getMessage}");
      return;
    }
    while (curAttr.get.isDefined) {
      val k = curAttr.get.get._1;
      val v = curAttr.get.get._2;
      XmlControlReader.setControlStringProperty(control.control, k, v);
      curAttr = reader.nextAttr();
      if (curAttr.isFailure) { System.err.println(s"Xml readStringProperty error:${curAttr.failed.get.getMessage}"); return; }
    }
  }
}

case class XmlElemControlReader(xmlElem: XmlElement,templateOwner: Option[BaseControl]) {

  def read(): Try[BaseControl] = {
     readXmlControl(xmlElem)
  }

  def readXmlControl(elem: XmlElement): Try[BaseControl] = Try {
    val control = XmlControl.tryCreate(elem.name).get.control;
    for (attr <- elem.attributes) {
      XmlControlReader.setControlStringProperty(control, attr._1, attr._2);
    }
    control
  }
}
