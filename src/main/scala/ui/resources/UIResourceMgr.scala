package ui.resources
import ui.xml.UISXmlEnv

object UIResourceMgr {
  var appResource: UIResource = UIResource.empty()
  def loadScriptResource(path: String): Unit = {
    val evalValue = UISXmlEnv.evalFile(path).get
    val resList = evalValue.toScalaValue().asInstanceOf[Vector[Any]]
    for(resItem <- resList) {
      this.appResource += resItem.asInstanceOf[BaseUIResource]
    }
  }
}
