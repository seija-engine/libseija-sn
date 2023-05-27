package ui.xml
import scala.collection.mutable;
import scala.collection.mutable.HashMap
case class XmlReadSetting(
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
}

object XmlReadSetting {
    val default:XmlReadSetting = XmlReadSetting(
        "ui.controls",
        HashMap()
    )
}