package ui.xml
import sxml.vm.XmlNode
import scala.util.Try
import core.reflect.TypeInfo
import sxml.vm.VMValue
import ui.controls.UIElement

class SXmlObjectParser(val nsResolver: XmlNSResolver = XmlNSResolver.default) {
  def parse(xml: XmlNode): Try[Any] = Try {
    xml.Name match
      case "string" | "String" => xml.child(0).toScalaValue()
      case _                   => this._parse(xml).get
  }

  def _parse(xml: XmlNode): Try[Any] = Try {
    val curTypeInfo = nsResolver.resolverTypeInfo(xml.Name).get
    val curObject = curTypeInfo.create()
    for((k,v) <- xml.attrs) {
        setStringProp(curTypeInfo,curObject,k,v)
    }
    curObject
  }

  def setStringProp(typInfo:TypeInfo,curObject:Any,key:String,value:VMValue):Unit = {
    val isUIElement = curObject.isInstanceOf[UIElement]
  }
}
