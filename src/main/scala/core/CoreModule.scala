package core

import scala.scalanative.unsafe.Ptr
import core.reflect.DynTypeConv


class CoreModule extends IModule {
  override def OnAdd(appPtr: Ptr[Byte]): Unit = {
     FFISeijaCore.addCoreModule(appPtr);
     DynTypeConv.init();
     DynTypeConv.scanPackage(math.Color);
  }

  override def updateECSPtr(worldPtr: Ptr[Byte]): Unit = {
    Time.timePtr = FFISeijaCore.coreWorldGetTime(worldPtr);
  }
}
