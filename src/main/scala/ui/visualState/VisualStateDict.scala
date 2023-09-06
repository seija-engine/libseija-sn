package ui.visualState
import ui.ContentProperty
import core.reflect.*
import scala.collection.immutable.HashMap
import sxml.vm.ClosureData
import ui.xml.IXmlObject

@ContentProperty("content")
class VisualStateDict extends IXmlObject derives ReflectType {
    var content:HashMap[String,ClosureData] = HashMap()

    override def OnAddContent(value: Any): Unit = {
    }
}