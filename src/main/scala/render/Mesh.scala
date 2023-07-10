package render
import core.{RawComponent,RawComponentBuilder};
import asset.Handle;
import core.Entity
import asset.HandleUntyped
class Mesh;

case class MeshBuilder(var mesh:Handle[Mesh] = null) extends RawComponentBuilder {
    def build(entity:Entity):Unit = {
       FFISeijaRender.renderEntityAddMesh(core.App.worldPtr,entity.id,mesh.id)
    }
}

given MeshComponent:RawComponent[Handle[Mesh]] with {
    type BuilderType = MeshBuilder;
    type RawType = Handle[Mesh]
    def builder():MeshBuilder = new MeshBuilder();
    def getRaw(entity:Entity,isMut:Boolean):Handle[Mesh] = { ??? }
}