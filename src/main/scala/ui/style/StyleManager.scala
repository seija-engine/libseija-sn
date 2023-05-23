package ui.style
import scala.util.Try
import core.xml.XmlElement
import scala.util.Success
import scala.util.Failure

object StyleManager {
    

    def loadFile(path:String):Try[Unit] = XmlElement.fromFile(path).map(loadXmlElement)

    def loadString(xmlString:String):Try[Unit] = XmlElement.fromString(xmlString).map(loadXmlElement)

    def loadXmlElement(xmlElement:XmlElement) = {
        for(styleElement <- xmlElement.children) {
           Style.fromXmlElement(styleElement) match
            case Failure(exception) => {
                println(s"read style error ${exception}")
            }
            case Success(style) => {
                this.addStyle(style);
            }
           
        }
    }

    def addStyle(style:Style) = {

    }
}