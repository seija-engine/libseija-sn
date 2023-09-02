package ui.xml
import sxml.vm.SXmlVM
import sxml.vm.VMValue
import scala.util.Try
import sxml.vm.ExternModule
import scala.collection.mutable
import scala.collection.immutable.HashMap

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
    uiModule
  }

  private def style(attr:VMValue,dict:VMValue):VMValue = {
    val scalaMap = attr.toScalaValue().asInstanceOf[HashMap[String,Any]]
    val typ = scalaMap("type")
    println(typ)
    println(scalaMap)
    VMValue.VMUserData(vm)
  }
}
