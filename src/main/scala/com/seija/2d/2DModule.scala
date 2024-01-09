package com.seija.`2d`
import scala.scalanative.unsafe.Ptr
import com.seija.core.IModule

class Module2D extends IModule {
    override def OnAdd(appPtr: Ptr[Byte]): Unit = {
        FFISeija2D.r2dAddModule(appPtr);
    }
}