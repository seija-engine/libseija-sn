package com.seija.asset
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
    def loadSync[T](path:String)(using v:IAssetType[T]):Option[Handle[T]] = {
        val ta = v.TYPEID.ta;
        val tb = v.TYPEID.tb;
        FFISeijaAsset.assetLoadSync(com.seija.core.App.worldPtr,path,ta,tb).map(_.typed())
    }

    def get[T](path:String,isWeak:Boolean):Option[Handle[T]] = {
        FFISeijaAsset.assetGetHandle(com.seija.core.App.worldPtr,path,isWeak).map(_.typed())
    }

    def unload(handle:HandleUntyped):Unit = {
        FFISeijaAsset.assetUnload(handle)
    }

    def typeId(string:String):Option[AssetTypeId] = {
        FFISeijaAsset.stringToUUID(string)
    }
}