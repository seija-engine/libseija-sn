package com.seija.render
import com.seija.core.{RawComponent, RawComponentBuilder};
import com.seija.asset.Handle;
import com.seija.core.Entity
import com.seija.asset.HandleUntyped
class Mesh;

case class MeshBuilder(var mesh:Handle[Mesh] = null) extends RawComponentBuilder {
    def build(entity:Entity):Unit = {
       FFISeijaRender.renderEntityAddMesh(com.seija.core.App.worldPtr,entity.id,mesh.id)
    }
}

given MeshComponent:RawComponent[Handle[Mesh]] with {
    type BuilderType = MeshBuilder;
    type RawType = Handle[Mesh]
    def builder():MeshBuilder = new MeshBuilder();
    def getRaw(entity:Entity,isMut:Boolean):Handle[Mesh] = { ??? }
}