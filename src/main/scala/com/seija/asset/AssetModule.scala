package com.seija.asset

import com.seija.core.IModule
import scala.scalanative.unsafe.Ptr

case class AssetModule(val resPath:String) extends IModule {
  override def OnAdd(appPtr: Ptr[Byte]): Unit = {
        FFISeijaAsset.addAssetModule(appPtr,resPath)
  }
}