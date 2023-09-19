package ui.xml
import sxml.vm.SXmlVM
import sxml.vm.VMValue
import scala.util.Try
import sxml.vm.ExternModule
import scala.collection.mutable.HashMap as MutHashMap
import scala.collection.immutable.HashMap
import ui.resources.Style
import scala.util.Failure
import scala.util.Success
import ui.resources.Setter

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
    uiModule
  }

  private def style(attr:VMValue,dict:VMValue):VMValue = {
    ui.resources.Style.loadFromValue(attr,dict) match
      case Failure(exception) => {
        System.err.println(exception.toString())
        VMValue.NIL()
      }
      case Success(value) => {
        VMValue.VMUserData(value)
      } 
  }

  private def setter(target:VMValue,value:VMValue):VMValue = {
    val targetName = target.toScalaValue().asInstanceOf[String]
    
    VMValue.VMUserData(Setter(null,value.toScalaValue(),targetName))
  }
}
