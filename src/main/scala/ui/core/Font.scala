package ui.core
import asset.Assets;
import asset.IAssetType

class Font;

given FontAssetType: IAssetType[Font] with {
    val TYPEID = Assets.typeId("088a59fc-efcc-4071-916c-a4dab4a87a3b").get;
}
