package ui.style
import core.xml.XmlElement;
import scala.util.Try
import scala.util.Success
import core.reflect.Assembly
import scala.util.Failure
import scala.collection.mutable.ListBuffer

case class Style(val typName:String,
                 val className:Option[String],
                 val propertyList:ListBuffer[PropertySet]);

case class PropertySet(val name:String,val value:Any);

object Style {
    def fromXmlElement(element:XmlElement):Try[Style] = {
        val forType = element.attributes.get("ForType")
                             .getOrElse(throw new Exception("ForType is miss"));
        val className:Option[String] = element.attributes.get("Class");
        val typInfo = Assembly.get(forType,true)
                              .getOrElse(throw new Exception(s"not found type:${forType}"));
        val propertyList:ListBuffer[PropertySet] = ListBuffer();
        for(elem <- element.children) {
            elem.name match {
                case "Setter" => {
                  val setName = elem.attributes.get("Key").getOrElse(throw  new Exception("Setter miss Name"));
                  val setValueString = elem.attributes.get("Value");
                  if(setValueString.isDefined) {
                    typInfo.fieldFromString(setName,setValueString.get) match
                        case Failure(exception) => System.err.println(exception.toString());
                        case Success(value) => {
                           propertyList.addOne(PropertySet(setName,value));
                        }
                    
                  } else {
                    
                  }
                }
                case _ => {}
            }
                
            
        }
        Success(Style(forType,className,propertyList))
    }
}