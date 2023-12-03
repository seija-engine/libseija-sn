package com.seija.ui.trigger
import com.seija.ui.ContentProperty
import com.seija.ui.xml.IXmlObject
import com.seija.ui.resources.IPostReadResource
import com.seija.core.reflect.{Assembly, ReflectType}
import com.seija.ui.controls.UIElement
import com.seija
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