package com.seija.render
import com.seija.asset.Handle
import com.seija.asset.IAssetType
import com.seija.asset.Assets
import com.seija.core.RawComponentBuilder
import com.seija.core.Entity
import com.seija.core.RawComponent

class Material;

given MaterialAssetType: IAssetType[Material] with {
    val TYPEID = Assets.typeId("9fb83fbe-b850-42e0-a58c-53da87bace04").get;
}

case class MaterialBuilder(var material:Handle[Material] = null) extends RawComponentBuilder {
    def build(entity:Entity):Unit = {
       FFISeijaRender.renderEntityAddMaterial(com.seija.core.App.worldPtr,entity.id,material.id)
    }
}

given MaterialComponent:RawComponent[Handle[Material]] with {
    type BuilderType = MaterialBuilder;
    type RawType = Handle[Material]
    def builder():MaterialBuilder = new MaterialBuilder();
    def getRaw(entity:Entity,isMut:Boolean):Handle[Material] = { ??? }
}