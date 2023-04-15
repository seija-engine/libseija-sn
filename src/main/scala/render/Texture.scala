package render

import asset.IAssetType
import java.util.UUID;
import asset.Assets
class Texture;

given TextureType: IAssetType[Texture] with {
    val TYPEID = Assets.typeId("9fb83fbe-b850-42e0-a58c-53da87aaaa05").get;
}
