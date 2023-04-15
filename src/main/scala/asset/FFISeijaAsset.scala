package asset
import core.LibSeija;
import scalanative.unsafe._
import asset.HandleUntyped


object FFISeijaAsset {
  private val assetAddModulePtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],CString,Unit]]("asset_add_module");
  private val assetGetHandlePtr = LibSeija.getFunc[CFuncPtr6[Ptr[Byte],CString,Boolean,Ptr[Long],Ptr[Long],Ptr[Long],Boolean]]("asset_get_handle");
  private val assetLoadSyncPtr = LibSeija.getFunc[CFuncPtr5[Ptr[Byte],CString,Long,Long,Ptr[Long],Boolean]]("asset_load_sync");
  private val stringToUUIDPtr = LibSeija.getFunc[CFuncPtr3[CString,Ptr[Long],Ptr[Long],Boolean]]("string_to_uuid");
  private val unloadPtr = LibSeija.getFunc[CFuncPtr4[Ptr[Byte],Long,Long,Long,Unit]]("asset_unload");
  //string_to_uuid
  def addAssetModule(appPtr:Ptr[Byte],resPath:String):Unit = Zone { implicit z =>
    assetAddModulePtr(appPtr,toCString(resPath))
  }

  def assetGetHandle(worldPtr:Ptr[Byte],assetPath:String,isWeak:Boolean):Option[HandleUntyped] = Zone { implicit z =>
    val id = stackalloc[Long]()
    val ta = stackalloc[Long]()
    val tb = stackalloc[Long]()
    if(assetGetHandlePtr(worldPtr,toCString(assetPath),isWeak,id,ta,tb)) {
      return Some(HandleUntyped(!id,!ta,!tb));
    } else {
      return None;
    }
  }

  def assetLoadSync(worldPtr:Ptr[Byte],assetPath:String,ta:Long,tb:Long):Option[HandleUntyped] = Zone { implicit z =>
    val id = stackalloc[Long]()
    if(assetLoadSyncPtr(worldPtr,toCString(assetPath),ta,tb,id)) {
      return Some(HandleUntyped(!id,ta,tb));
    } else {
      println("222");
      return None;
    }
  }

  def assetUnload(handle:HandleUntyped):Unit = unloadPtr(core.App.worldPtr,handle.id,handle.ta,handle.tb)

  def stringToUUID(str:String):Option[AssetTypeId] = Zone { implicit z =>
    val ta = stackalloc[Long]()
    val tb = stackalloc[Long]()
    if(stringToUUIDPtr(toCString(str),ta,tb)) {
      return Some(AssetTypeId(!ta,!tb));
    } else {
      return None;
    }
  }

}
