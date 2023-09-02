package ui.resources
import core.logError
import ui.xml.XmlObjectParser
import ui.xml.XmlNSResolver
import core.xml.XmlElement
import ui.xml.UISXmlEnv

object UIResourceMgr {
  var appResource: UIResource = UIResource.empty()

  def loadResource(xmlPath: String): Unit = {
    val xmlElement = XmlElement.fromFile(xmlPath).logError()
    if (xmlElement.isFailure) return
    val parseObject =
      XmlObjectParser(XmlNSResolver.default).parse(xmlElement.get)
    if (parseObject.isFailure) return
    val uiRes = parseObject.get.asInstanceOf[UIResource]
    // for(res <- uiRes.resList) {
    //  this.appResource.addOne(res);
    // }
  }

  def loadScriptResource(path: String): Unit = {
    val evalValue = UISXmlEnv.evalFile(path).get
    //println(s"evalValue:${evalValue}")
  }
}
