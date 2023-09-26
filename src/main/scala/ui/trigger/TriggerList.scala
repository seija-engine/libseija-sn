package ui.trigger
import ui.ContentProperty
import ui.xml.IXmlObject
import ui.resources.IPostReadResource
import core.reflect.{Assembly, ReflectType}
import ui.controls.UIElement

import scala.collection.mutable.ArrayBuffer
import scala.util.{Failure, Success}

@ContentProperty("content")
class TriggerList extends IXmlObject with IPostReadResource derives ReflectType {
    var content:ArrayBuffer[Any] = ArrayBuffer.empty
    protected var triggerList:ArrayBuffer[PropTrigger] = ArrayBuffer.empty
    override def OnAddContent(value: Any): Unit = {
      val triggerList = value.asInstanceOf[Vector[Any]]
      val propName = triggerList(0)
      val lst = triggerList(1).asInstanceOf[Vector[Any]]
      PropTrigger.fromScript(propName, lst) match
        case Failure(exception) => slog.error(exception)
        case Success(value) =>
          this.triggerList += value

    }

    override def OnPostReadResource(): Unit = {
        this.triggerList.foreach(_.OnPostReadResource())
    }

    def onPropChanged(element:UIElement,propName:String):Unit = {
      if(this.content.isEmpty) return;
      val typeInfo = Assembly.getTypeInfo(element)
      if (typeInfo.isEmpty) return;
      for(propTrigger <- this.triggerList) {
        if(propTrigger.propName == propName) {
          propTrigger.onPropChanged(element,typeInfo.get)
        }
      }
    }
}