package com.seija.render

import com.seija.asset.IAssetType
import java.util.UUID;
import com.seija.asset.Assets
class Texture;

given TextureAssetType: IAssetType[Texture] with {
    val TYPEID = Assets.typeId("9fb83fbe-b850-42e0-a58c-53da87aaaa05").get;
}
