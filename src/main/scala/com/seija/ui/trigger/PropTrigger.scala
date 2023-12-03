package com.seija.ui.trigger

import com.seija.core.reflect.TypeInfo

import scala.util.{Failure, Success, Try}
import com.seija.ui.controls.UIElement
import com.seija.ui.resources.{IPostReadResource, Style,Setter}
import com.seija.ui.xml.UISXmlEnv
import com.seija
import scala.collection.mutable.HashMap as MutHashMap
import scala.collection.immutable.HashMap
import scala.collection.mutable.ArrayBuffer

case class PropTrigger(propName:String,valueDict:MutHashMap[Any,ArrayBuffer[Setter]]) extends IPostReadResource {
  def onPropChanged(element:UIElement, typInfo:TypeInfo):Unit = {
    val newValue = typInfo.getFieldTry(propName).get.get(element)
    this.valueDict.get(newValue.toString) match
      case Some(setterList) => setterList.foreach { setter =>
        setter.applyTo(typInfo,element)
      }
      case None =>
  }

  override def OnPostReadResource(): Unit = {
    this.valueDict.values.foreach(_.foreach(_.OnPostReadResource()))
  }
}

object PropTrigger {
    def fromScript(prop:Any,anyList:Vector[Any]):Try[PropTrigger] = Try {
        val typeInfo:TypeInfo = UISXmlEnv.getGlobal("*type-info*").get.asInstanceOf[TypeInfo]
        val propName = prop.asInstanceOf[String]
        val setterDict:MutHashMap[Any,ArrayBuffer[Setter]] = MutHashMap.empty
        for(idx <- 0.until(anyList.length,2)) {
          val valueKey = anyList(idx)
          val valueDict = anyList(idx + 1).asInstanceOf[HashMap[String,Any]]
          Style.readSetterList(valueDict,typeInfo) match
            case Failure(exception) => slog.error(exception)
            case Success(value) => { setterDict.put(valueKey,value) }
        }
        PropTrigger(propName,setterDict)
    }
}