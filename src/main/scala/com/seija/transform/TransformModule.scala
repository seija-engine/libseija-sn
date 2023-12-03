package com.seija.transform

import com.seija.core.IModule
import scala.scalanative.unsafe.Ptr

class TransformModule extends IModule {

  override def OnAdd(appPtr: Ptr[Byte]): Unit = {
    FFISeijaTransform.addTransformModule(appPtr)
  }

    
}
