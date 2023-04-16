package ui

import asset.IAssetType
import asset.Assets

class SpriteSheet;

given SpriteSheetAsset:IAssetType[SpriteSheet] with {
    val TYPEID = Assets.typeId("26a121e6-a1bc-d805-3452-831772db38db").get
}



extension (t:RawSpriteSheet) {
    def getIndex(name:String):Option[Int] = {
        val index = FFISeijaUI.spritesheetGetIndex(t,name);
        if(index < 0) { return None; }
        return Some(index)
    }
}