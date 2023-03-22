package render
import core.IModule;

final case class RenderModule(val config:RenderConfig) extends IModule {
    def OnAdd(app: core.App): Unit = {
        FFISeijaRender.addRenderModule(config.toPtr(), app.ptr)
    }
}
