package com.seija.ui.xml
import com.seija.sxml.vm.SXmlVM
import com.seija.sxml.vm.VMValue
import scala.util.Try
import com.seija.sxml.vm.ExternModule
import scala.collection.mutable.HashMap as MutHashMap
import scala.collection.immutable.HashMap
import com.seija.ui.resources.Style
import scala.util.Failure
import scala.util.Success
import com.seija.ui.resources.Setter
import com.seija.ui.controls.DataTemplate
import com.seija.sxml.vm.XmlNode
import com.seija.ui.resources.UIResourceMgr
import com.seija.ui.trigger.PropTrigger
import com.seija

object UISXmlEnv {
  private val vm: SXmlVM = SXmlVM()
  private val envValueDict:MutHashMap[String,Any] = MutHashMap.empty

  def init(): Unit = {
    vm.addBuildinModule()
    vm.env.addExternModule(uiExternModule())
  }

  def evalFile(path: String): Try[VMValue] = this.vm.callFile(path)

  def setGlobal(key:String,value:Any):Unit = {
    if(value == null) {
      this.envValueDict.remove(key)
    } else {
      this.envValueDict.put(key,value)
    }
  }

  def getGlobal(key:String):Option[Any] = this.envValueDict.get(key)

  def uiExternModule():ExternModule = {
    val uiModule = ExternModule("ui",MutHashMap.empty)
    uiModule.addFunc(style,true) 
    uiModule.addFunc(setter,true)
    uiModule.addFunc(setterKV,true)
    uiModule.addFunc(res,true)
    uiModule
  }

  private def style(attr:VMValue,dict:VMValue):VMValue = {
    com.seija.ui.resources.Style.loadFromValue(attr,dict) match
      case Failure(exception) => {
        slog.error(exception)
        VMValue.NIL()
      }
      case Success(value) => {
        VMValue.VMUserData(value)
      } 
  }

  private def setterKV(key:VMValue,target:VMValue,value:VMValue):VMValue = {
    val targetName = target.toScalaValue().asInstanceOf[String]
    val strKey = key.toScalaValue().asInstanceOf[String]
    VMValue.VMUserData(Setter(strKey,value.toScalaValue(),targetName))
  }

  private def setter(target:VMValue,value:VMValue):VMValue = {
    val targetName = target.toScalaValue().asInstanceOf[String]
    VMValue.VMUserData(Setter(null,value.toScalaValue(),targetName))
  }

  private def res(value:VMValue):VMValue = {
    VMValue.VMUserData(ResKey(value.toScalaValue().asInstanceOf[String]))
  }


}

case class ResKey(resName:String)