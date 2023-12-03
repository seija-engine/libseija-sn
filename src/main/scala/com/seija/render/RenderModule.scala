package com.seija.render
import com.seija.core.IModule;
import scala.scalanative.unsafe.Ptr

final case class RenderModule(val config:RenderConfig) extends IModule {
    def OnAdd(appPtr: Ptr[Byte]): Unit = {
        val configPtr = config.toPtr();
        FFISeijaRender.addRenderModule(configPtr, appPtr)
    }
}
