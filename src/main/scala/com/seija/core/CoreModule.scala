package com.seija.core

import scala.scalanative.unsafe.Ptr
import com.seija.core.reflect.DynTypeConv


class CoreModule extends IModule {
  override def OnAdd(appPtr: Ptr[Byte]): Unit = {
     FFISeijaCore.addCoreModule(appPtr);
     DynTypeConv.init();
     DynTypeConv.scanPackage(com.seija.math.Color);
  }

  override def updateECSPtr(worldPtr: Ptr[Byte]): Unit = {
    Time.timePtr = FFISeijaCore.coreWorldGetTime(worldPtr);
  }
}
