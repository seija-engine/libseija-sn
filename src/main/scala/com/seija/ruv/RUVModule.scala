package com.seija.ruv
import com.seija.core.IModule
import scala.scalanative.unsafe.Ptr

class RUVModule extends IModule {
  override def OnAdd(appPtr: Ptr[Byte]): Unit = { }

  override def update(): Unit = {
    RuvRuntime.update();
  }

}