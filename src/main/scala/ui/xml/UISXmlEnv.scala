package ui.xml
import sxml.vm.SXmlVM
import sxml.vm.VMValue
import scala.util.Try
import sxml.vm.ExternModule
import scala.collection.mutable
import scala.collection.immutable.HashMap
import ui.resources.Style
import scala.util.Failure
import scala.util.Success

object UISXmlEnv {
  private val vm: SXmlVM = SXmlVM()

  def init(): Unit = {
    vm.addBuildinModule()
    vm.env.addExternModule(uiExternModule())
  }

  def evalFile(path: String): Try[VMValue] = this.vm.callFile(path)


  def uiExternModule():ExternModule = {
    val uiModule = ExternModule("ui",mutable.HashMap.empty)
    uiModule.addFunc(style,true) 
    uiModule.addFunc(target,true)
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

  private def target(fst:VMValue,t:VMValue,v:VMValue):VMValue = {
    VMValue.NIL()
  }
}
