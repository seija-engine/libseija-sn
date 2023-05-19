package core.xml
import scala.util.Try
import scala.collection.mutable.ListBuffer

case class XmlElement(
    val name:String, 
    val attributes:Map[String, String], 
    val children:List[XmlElement]);


object XmlElement {
    def fromString(xmlString:String):Try[XmlElement]  = {
        val reader = XmlReader.fromString(xmlString)
        XmlElementReader(reader).read()
    }
}

case class XmlElementReader(reader:XmlReader) {
    private var elementStack:ListBuffer[XmlElement] = ListBuffer[XmlElement]()

    def read():Try[XmlElement] = {
      var curEvent = reader.nextEvent();
      
      ???
    }
}