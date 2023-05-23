package ui.style
import core.xml.XmlElement;
import scala.util.Try
import scala.util.Success
import core.reflect.Assembly
import scala.util.Failure
import scala.collection.mutable.ListBuffer
import core.reflect.*;
import core.reflect.tryInto
import core.reflect.Assembly.nameOf

class MissRequiredFieldException(className:String,name:String) extends Exception(s"miss required field:${className}.${name}");
class ReadSetterException(key:String,value:String) extends Exception(s"read setter error:${key} = ${value}");

case class Style(val typName:String,
                 val className:Option[String],
                 val propertyList:ListBuffer[PropertySet]);

case class PropertySet(val name:String,val value:Any);

object Style {
    def fromXmlElement(element:XmlElement):Try[Style] = tryInto(element);

    given Into[XmlElement,Style] with {
      override def into(element: XmlElement): Style = {
        val forType = element.attributes.get("ForType").getOrElse(throw new MissRequiredFieldException("Style","ForType"));
        val className:Option[String] = element.attributes.get("Class");
        val typInfo = Assembly.getOrThrow(forType,true);
        val propertyList:ListBuffer[PropertySet] = ListBuffer();
        for(elem <- element.children) {
            elem.name match {
                case "Setter" => {
                  val setName = elem.attributes.get("Key").getOrElse(throw new MissRequiredFieldException("Setter","Key"));
                  elem.attributes.get("Value") match {
                    case Some(value) => {
                        val newProprty = for {
                          info <- typInfo.getField(setName)
                          convValue <- DynTypeConv.strConvertTo(info.typName,value)
                        } yield PropertySet(setName,convValue.get);
                        propertyList += newProprty.getOrElse(throw new ReadSetterException(setName,value));
                    }
                    case None => {
                       
                    }
                  }
                }
                case _ => {}
            }
        }
        Style(forType,className,propertyList)
      }
    }
}