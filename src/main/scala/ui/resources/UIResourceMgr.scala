package ui.resources
import ui.xml.UISXmlEnv
import sxml.vm.XmlNode
import ui.xml.SXmlObjectParser
import ui.xml.XmlNSResolver
import scala.util.Failure
import scala.util.Success

object UIResourceMgr {
  var appResource: UIResource = UIResource.empty()
  
  def loadScriptResource(path: String): Unit = {
    val evalValue = UISXmlEnv.evalFile(path).get
    val resList = evalValue.toScalaValue().asInstanceOf[Vector[Any]]
    for(resItem <- resList) {
      resItem match
        case xml:XmlNode => {
          SXmlObjectParser(XmlNSResolver.default).parse(xml) match
            case Failure(exception) => slog.error(exception)
            case Success(value) => this.appResource += value.asInstanceOf[BaseUIResource]
        }
        case res:BaseUIResource => this.appResource += resItem.asInstanceOf[BaseUIResource]
        case other => slog.error(s"not support res typ:${other}")
    }

    resList.foreach {
      case v:IPostReadResource => v.OnPostReadResource()
      case _ => 
    }
  }
}
