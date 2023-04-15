package asset
import java.util.UUID;

case class AssetTypeId(ta:Long,tb:Long)
case class HandleUntyped(id:Long,ta:Long,tb:Long) {
    def typed[T]() = Handle[T](this);
}
case class Handle[T](val id:HandleUntyped)

trait IAssetType[T] {
    val TYPEID:AssetTypeId;
}

object Assets {
    def loadSync[T](path:String)(using v:IAssetType[T]):Option[HandleUntyped] = {
        val ta = v.TYPEID.ta;
        val tb = v.TYPEID.tb;
        FFISeijaAsset.assetLoadSync(core.App.worldPtr,path,ta,tb)
    }

    def get(path:String,isWeak:Boolean):Option[HandleUntyped] = {
        FFISeijaAsset.assetGetHandle(core.App.worldPtr,path,isWeak)
    }

    def unload(handle:HandleUntyped):Unit = {
        FFISeijaAsset.assetUnload(handle)
    }

    def typeId(string:String):Option[AssetTypeId] = {
        FFISeijaAsset.stringToUUID(string)
    }
}