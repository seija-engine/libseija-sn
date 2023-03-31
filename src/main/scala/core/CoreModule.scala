package core

import scala.scalanative.unsafe.Ptr


class CoreModule extends IModule {
  override def OnAdd(appPtr: Ptr[Byte]): Unit = {
     FFISeijaCore.addCoreModule(appPtr);
  }
}
