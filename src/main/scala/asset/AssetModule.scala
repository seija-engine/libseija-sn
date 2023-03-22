package asset

import core.IModule

case class AssetModule(val resPath:String) extends IModule {
  override def OnAdd(app: core.App): Unit = {
        FFISeijaAsset.addAssetModule(app.ptr,resPath)
  }
}