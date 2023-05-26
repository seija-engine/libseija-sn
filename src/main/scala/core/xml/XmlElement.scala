package core.xml
import scala.util.Try
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashMap
import scala.util.Success
import scala.util.Failure
import scala.util.boundary,boundary.break

case class XmlElement(
    val name: String,
    val attributes: HashMap[String, String],
    val children: ListBuffer[XmlElement],
    var innerText: Option[String]
) {

  override def toString(): String = {
    var builder: StringBuffer = StringBuffer();
    this.toXMLString(0, builder);
  }

  def toXMLString(dep: Int, builder: StringBuffer): String = {
    val appendLine = () => (0 until dep).foreach(_ => builder.append(' '))
    appendLine();
    builder.append(s"<${this.name}");
    for ((key, value) <- this.attributes) {
      builder.append(s" \"${key}\"=\"${value}\"")
    }
    builder.append(">\r\n");
    for (child <- this.children) {
      child.toXMLString(dep + 1, builder)
    }
    if(this.innerText.isEmpty) {
      builder.append(this.innerText.get)
    }
    appendLine();
    builder.append(s"</${this.name}>\r\n");
    builder.toString()
  }
}

object XmlElement {
  def fromString(xmlString: String): Try[XmlElement] = {
    val reader = XmlReader.fromString(xmlString)
    val readElement = XmlElementReader(reader).read();
    reader.release();
    readElement
  }

  def fromFile(filePath: String): Try[XmlElement] = {

    val reader = XmlReader.fromFile(filePath).get;
    val readElement = XmlElementReader(reader).read()
    reader.release();
    readElement
  }
}

case class XmlElementReader(reader: XmlReader) {
  private var elementStack: ListBuffer[XmlElement] = ListBuffer[XmlElement]()

  def read(): Try[XmlElement] = Try {
    var curEvent = reader.nextEvent().get;
    val breakValue = boundary {
      while (!curEvent.IsEOF()) {
        curEvent match {
          case XmlEvent.StartElement(name) => {
            val attrMap = this.readAttrToMap() match
              case Failure(exception) => {
                System.err.println(exception); HashMap()
              }
              case Success(value) => { value }

            var newElement = XmlElement(name, attrMap, ListBuffer(), None)
            elementStack.addOne(newElement);
          }
          case XmlEvent.EndElement(name) => {
            val curElement = elementStack.remove(elementStack.length - 1)
            if (elementStack.isEmpty) {
              break(Success(curElement))
            } else {
              elementStack.last.children.addOne(curElement)
            }
          }
          case XmlEvent.EmptyElement(name) => {
            val attrMap = this.readAttrToMap() match
              case Failure(exception) => {
                System.err.println(exception); HashMap()
              }
              case Success(value) => { value }
            val newElement = XmlElement(name, attrMap, ListBuffer(), None);
            if (elementStack.isEmpty) {
              break(Success(newElement));
            } else {
              elementStack.last.children.addOne(newElement)
            }
          }
          case XmlEvent.Text(text) => {
            val trimText = text.trim();
            if(!trimText.isEmpty()) elementStack.last.innerText = Some(trimText);
          }
          case XmlEvent.Comment(text) =>
          case XmlEvent.EOF           =>
          case XmlEvent.Unkonwn => break(Exception("unkonwn xml"))
        }
        curEvent = reader.nextEvent().get
      }
    }
    breakValue match
      case Success(value) => value
      case e:Exception => throw e
      case _ => this.elementStack.remove(0)
  }

  def readAttrToMap(): Try[HashMap[String, String]] = Try {
    var curAttr = reader.nextAttr()
    var attrMap: HashMap[String, String] = HashMap();
    while (curAttr.get.isDefined) {
      val k = curAttr.get.get._1;
      val v = curAttr.get.get._2;
      attrMap.put(k, v);
      curAttr = reader.nextAttr();
    }
    attrMap
  }
}
