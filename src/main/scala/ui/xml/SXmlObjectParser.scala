package ui.xml
import sxml.vm.XmlNode
import core.logError
import scala.util.Try
import core.reflect.{DynTypeConv, TypeInfo}
import sxml.vm.VMValue
import ui.binding.BindingItem
import ui.controls.UIElement
import ui.resources.UIResourceMgr

class SXmlObjectParser(val nsResolver: XmlNSResolver = XmlNSResolver.default) {
  def parse(xml: XmlNode): Try[Any] = Try {
    xml.Name match
      case "string" | "String" => xml.child(0).toScalaValue()
      case _                   => this._parse(xml).get
  }

  def _parse(xml: XmlNode): Try[Any] = Try {

    val curTypeInfo = nsResolver.resolverTypeInfo(xml.Name).get
    val curObject = curTypeInfo.create()

    for ((k, v) <- xml.attrs) {
      setStringProp(curTypeInfo, curObject, k, v).logError()
    }

    var contentCount = 0;
    for(childElem <- xml.child) {
      
    }

    curObject
  }

  def setStringProp(typInfo: TypeInfo,curObject: Any,key: String,value: VMValue): Try[Unit] = Try {
    val isUIElement = curObject.isInstanceOf[UIElement]
    val stringValue: String = value.unwrap[VMValue.VMString]().get.value
    if (isUIElement && key.startsWith("{Binding")) {
      val curUIElement = curObject.asInstanceOf[UIElement]
      curUIElement.addBindItem(BindingItem.parse(key, stringValue).get)
    } else if (key.startsWith("{Res")) {
      val startLen = "{Res".length()
      var resName = stringValue.substring(startLen, stringValue.length - 1)
      resName = resName.trim()
      this.setRes(resName, typInfo, key, curObject)
    } else {
      (for {
        filed <- typInfo.getFieldTry(key)
        value <- DynTypeConv.strConvertToTry(filed.typName, stringValue)
      } yield filed.set(curObject, value)).get
    }
  }

  def setRes(resName: String,typeInfo: TypeInfo,key: String,curObject: Any): Unit = {
    UIResourceMgr.appResource.findRes(resName).foreach { res =>
      typeInfo.getField(key).foreach { f =>
        DynTypeConv
          .convertStrTypeTry(res.getClass.getName, f.typName, res)
          .foreach { v =>
            f.set(curObject, v)
          }
      }
    }
  }

}
