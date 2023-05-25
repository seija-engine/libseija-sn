package ui

import _root_.core.IModule
import scalanative.unsafe._
import ui.core.FFISeijaUI
import _root_.core.reflect.DynTypeConv
import _root_.core.reflect.Assembly


final case class UIModule() extends IModule {
    def OnAdd(appPtr: Ptr[Byte]): Unit = {
        FFISeijaUI.addSpriteSheetModule(appPtr);
        FFISeijaUI.addUIModule(appPtr);
        DynTypeConv.scanPackage(ui.controls2.Image);
        DynTypeConv.scanPackage(ui.Template);
        DynTypeConv.scanPackage(ui.core.Thickness);
        
        Assembly.scanPackage(ui.controls2.Image); 
    }

    override def update(): Unit = {
        EventManager.update();
    }
}