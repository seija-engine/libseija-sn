package input
import core.IModule;
class InputModule extends IModule {
  override def OnAdd(app: core.App): Unit = {
    FFISeijaInput.addInputModule(app.ptr);
  }
}
