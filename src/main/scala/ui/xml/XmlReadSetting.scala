package ui.xml
import scala.collection.mutable;
import scala.collection.mutable.HashMap
import core.reflect.*;
case class XmlReadSetting(
    specialNS:HashMap[String,String] = HashMap(),
    defaultNS:String,
    nsAlias:HashMap[String,String] = HashMap()
) {

    def toFullName(aliasName:String):Option[String] = {
        val splitNames = aliasName.split(':');
        if(splitNames.length > 1) {
            if(nsAlias.contains(splitNames(0))) {
              Some(s"${nsAlias(splitNames(0))}.${splitNames(1)}")
            } else { None }
        } else {
           Some(s"${defaultNS}.${splitNames(0)}")
        }
    }

    def getTypeInfo(xmlName:String):Option[TypeInfo] = {
        val splitNames = xmlName.split(':');
        if(splitNames.length > 1) {
            nsAlias.get(splitNames(0)).flatMap {ns => Assembly.get(s"${ns}.${splitNames(1)}") }
        } else {
           
           None
        }
    }
}

object XmlReadSetting {
    val default:XmlReadSetting = XmlReadSetting(
        HashMap("Style" -> "ui.controls"),
        "ui.controls",
        HashMap()
    )
}