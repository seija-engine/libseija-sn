package ui.trigger
import ui.ContentProperty
import ui.xml.IXmlObject
import ui.resources.IPostReadResource
import core.reflect.ReflectType

@ContentProperty("content")
class TriggerList extends IXmlObject with IPostReadResource derives ReflectType {
    var content:Vector[PropTrigger] = Vector()

    override def OnAddContent(value: Any): Unit = { }

    override def OnPostReadResource(): Unit = {
        
    }
}