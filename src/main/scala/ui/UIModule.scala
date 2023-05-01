package ui

import _root_.core.IModule
import scalanative.unsafe._
import ui.core.FFISeijaUI


final case class UIModule() extends IModule {
    def OnAdd(appPtr: Ptr[Byte]): Unit = {
        FFISeijaUI.addSpriteSheetModule(appPtr);
        FFISeijaUI.addUIModule(appPtr);
    }

    override def update(): Unit = {
        EventManager.update();
    }
}