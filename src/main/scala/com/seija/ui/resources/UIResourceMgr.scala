package com.seija.ui.resources
import com.seija.ui.xml.UISXmlEnv
import com.seija.sxml.vm.XmlNode
import com.seija.ui.xml.SXmlObjectParser
import com.seija.ui.xml.XmlNSResolver
import scala.util.Failure
import scala.util.Success
import com.seija
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
        case res:BaseUIResource => this.appResource += res
        case other => slog.error(s"not support res typ:${other}")
    }

    resList.foreach {
      case v:IPostReadResource => v.OnPostReadResource()
      case _ => 
    }
  }
}
