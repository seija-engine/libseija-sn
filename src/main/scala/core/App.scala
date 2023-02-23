package core;
import scalanative.unsafe._
import scalanative.unsigned.UnsignedRichInt
import scala.scalanative.unsigned.UInt
import ffi._;
trait IModule {
  def OnAdd(app:App):Unit;
}

case class App(val ptr:Ptr[Byte]) extends AnyVal {
  def setFPS(fps:UInt) = FFISeijaApp.appSetFPS(ptr,fps)
  def start() = FFISeijaApp.appStart(ptr)
  def run() = FFISeijaApp.appRun(ptr)

  def addModule(module:IModule):Unit = {
      module.OnAdd(this);
  }
}

object App {
  def apply():App = {
    val appPtr = FFISeijaApp.appNew();
    App(appPtr)
  }
}