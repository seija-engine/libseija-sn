package render
import asset.Handle
import asset.IAssetType
import asset.Assets
import core.RawComponentBuilder
import core.Entity
import core.RawComponent

class Material;

given MaterialAssetType: IAssetType[Material] with {
    val TYPEID = Assets.typeId("9fb83fbe-b850-42e0-a58c-53da87bace04").get;
}

case class MaterialBuilder(var material:Handle[Material] = null) extends RawComponentBuilder {
    def build(entity:Entity):Unit = {
       FFISeijaRender.renderEntityAddMaterial(core.App.worldPtr,entity.id,material.id)
    }
}

given MaterialComponent:RawComponent[Handle[Material]] with {
    type BuilderType = MaterialBuilder;
    type RawType = Handle[Material]
    def builder():MaterialBuilder = new MaterialBuilder();
    def getRaw(entity:Entity,isMut:Boolean):Handle[Material] = { ??? }
}