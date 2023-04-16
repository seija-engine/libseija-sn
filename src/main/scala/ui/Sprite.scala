package ui

import core.RawComponentBuilder
import core.Entity
import core.RawComponent
import scala.scalanative.runtime.RawPtr
import scala.scalanative.unsafe.Ptr
import asset.Handle
import math.Vector4

class Sprite;

class SpriteBuilder extends RawComponentBuilder {
    var spriteIndex:Int = 0;
    var atlas:Handle[SpriteSheet] = null;
    var color:Vector4 = Vector4.one;
    def build(entity: Entity): Unit = {
        FFISeijaUI.entityAddSpriteSimple(core.App.worldPtr,entity.id,spriteIndex,atlas.id.id,color);
    }
}


given SpriteComponent:RawComponent[Sprite] with  {
  type BuilderType = SpriteBuilder;
  type RawType = Ptr[Byte]
  override def builder(): BuilderType = new SpriteBuilder()

  override def getRaw(entity: Entity): RawType = ???


}