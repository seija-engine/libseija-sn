package core;
import scalanative.unsafe._
import scalanative.unsigned.UnsignedRichInt
import scala.scalanative.unsigned.UInt
import input.Input
import scala.collection.mutable;

trait IModule {
  def OnAdd(appPtr:Ptr[Byte]):Unit;

  def updateECSPtr(worldPtr:Ptr[Byte]):Unit = {};

  def update():Unit = {}
}

trait IGameApp {
  def OnStart():Unit;
  def OnUpdate():Unit;
}

object App {
  val appPtr = FFISeijaApp.appNew();
  var worldPtr:Ptr[Byte] = null;
  var moduleList:mutable.ArrayBuffer[IModule]  = new mutable.ArrayBuffer();
  var gameApp:IGameApp = null;

  def setFPS(fps:UInt) = FFISeijaApp.appSetFPS(appPtr,fps)

  def start(gameApp:IGameApp) = {
    this.gameApp = gameApp;
    FFISeijaCore.appSetOnStart(appPtr,CFuncPtr.toPtr(CFuncPtr1.fromScalaFunction(App.OnStart)));
    FFISeijaCore.appSetOnUpdate(appPtr,CFuncPtr.toPtr(CFuncPtr1.fromScalaFunction(App.OnUpdate)));
    FFISeijaApp.appStart(appPtr)
  }

  def run() = FFISeijaApp.appRun(appPtr)

  def addModule(module:IModule):Unit = {
      module.OnAdd(appPtr);
      this.moduleList += module;
  }

  def OnStart(worldPtr:Ptr[Byte]) = {
    App.worldPtr = worldPtr;
    this.moduleList.foreach(_.updateECSPtr(worldPtr))

    this.gameApp.OnStart();
  }

  def OnUpdate(worldPtr:Ptr[Byte]) = {
    this.moduleList.foreach((module) => {
      module.updateECSPtr(worldPtr)
      module.update();
    })
    
    
    this.gameApp.OnUpdate();
  }
}