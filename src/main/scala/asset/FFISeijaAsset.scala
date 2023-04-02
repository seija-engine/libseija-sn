package asset
import core.LibSeija;
import scalanative.unsafe._

object FFISeijaAsset {
  private val assetAddModulePtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],CString,Unit]]("asset_add_module");

  def addAssetModule(appPtr:Ptr[Byte],resPath:String):Unit = Zone { implicit z =>
    assetAddModulePtr(appPtr,toCString(resPath))
  }
}
