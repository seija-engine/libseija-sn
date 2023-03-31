package render
import core.IModule;
import scala.scalanative.unsafe.Ptr

final case class RenderModule(val config:RenderConfig) extends IModule {
    def OnAdd(appPtr: Ptr[Byte]): Unit = {
        FFISeijaRender.addRenderModule(config.toPtr(), appPtr)
    }
}
