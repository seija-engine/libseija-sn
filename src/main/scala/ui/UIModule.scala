package ui
import _root_.core.IModule
import _root_.core.reflect.{Assembly, DynTypeConv}
import ui.controls.Image
import ui.core.SizeValue.*
import ui.core.{FFISeijaUI, SizeValue}
import ui.event.EventManager
import ui.xml.UISXmlEnv

import scala.annotation.StaticAnnotation
import scala.scalanative.unsafe.*

case class ContentProperty(val name: String) extends StaticAnnotation

final case class UIModule() extends IModule {
  def OnAdd(appPtr: Ptr[Byte]): Unit = {
    UISXmlEnv.init()
    FFISeijaUI.addSpriteSheetModule(appPtr);
    FFISeijaUI.addUIModule(appPtr);
    DynTypeConv.scanPackage(ui.controls.Image);
    DynTypeConv.scanPackage(ui.Atlas);
    DynTypeConv.scanPackage(ui.core.Thickness);
    DynTypeConv.scanPackage(ui.command.FCommand);
    DynTypeConv.register[scala.Float, SizeValue];
    Assembly.scanPackage(ui.controls.Image);
    Assembly.scanPackage(ui.resources.UIResource);
    Assembly.scanPackage(ui.visualState.ViewStates);
  }

  override def updateECSPtr(worldPtr: Ptr[CSignedChar]): Unit = {
    LayoutUtils.init(worldPtr)
  }

  override def update(): Unit = {
    LayoutUtils.OnUpdate()
    EventManager.update()
  }
}
