package ui

import _root_.core.IModule
import scalanative.unsafe._
import ui.core.FFISeijaUI
import _root_.core.reflect.DynTypeConv
import _root_.core.reflect.Assembly
import scala.annotation.StaticAnnotation
import ui.controls.Image
import ui.core.SizeValue._
import ui.core.SizeValue;

case class ContentProperty(val name:String) extends StaticAnnotation;

final case class UIModule() extends IModule {
    def OnAdd(appPtr: Ptr[Byte]): Unit = {
        FFISeijaUI.addSpriteSheetModule(appPtr);
        FFISeijaUI.addUIModule(appPtr);
        DynTypeConv.scanPackage(ui.controls.Image);
        DynTypeConv.scanPackage(ui.Atlas);
        DynTypeConv.scanPackage(ui.core.Thickness);
        DynTypeConv.scanPackage(ui.command.FCommand);
        DynTypeConv.register[scala.Float,SizeValue];
        Assembly.scanPackage(ui.controls.Image);
        Assembly.scanPackage(ui.resources.UIResource);
        Assembly.scanPackage(ui.visualState.VisualState);
    }

    override def update(): Unit = {
        EventManager.update();
    }
}