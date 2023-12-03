package com.seija.input
import com.seija.core.IModule;
import scala.scalanative.unsafe.Ptr
class InputModule extends IModule {
  override def OnAdd(appPtr: Ptr[Byte]): Unit = {
    FFISeijaInput.addInputModule(appPtr);
  }

  override def updateECSPtr(worldPtr: Ptr[Byte]): Unit = {
    Input.ptr = FFISeijaInput.worldGetInput(worldPtr);
  }
}
