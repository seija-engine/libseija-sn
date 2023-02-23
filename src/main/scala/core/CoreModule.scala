package core


class CoreModule extends IModule {
  override def OnAdd(app: App): Unit = {
     FFISeijaCore.addCoreModule(app.ptr);
  }
}
