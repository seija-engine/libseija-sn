package ui.core

import _root_.core.RawComponentBuilder
import _root_.core.{Entity,App}
import _root_.core.RawComponent
import scala.scalanative.runtime.RawPtr
import scala.scalanative.unsafe.Ptr
import asset.Handle
import math.Vector4
import ui.core.Thickness
import ui.core.FFISeijaUI
import ui.core.SpriteSheet


class Sprite;

enum SpriteType {
  case Simple
  case Slice(border:Thickness)
}

class SpriteBuilder extends RawComponentBuilder {
    var spriteIndex:Int = 0;
    var atlas:Handle[SpriteSheet] = null;
    var color:Vector4 = Vector4.one;
    var typ:SpriteType = SpriteType.Simple
    def build(entity: Entity): Unit = {
        typ match
          case SpriteType.Simple => {
            FFISeijaUI.entityAddSpriteSimple(App.worldPtr,entity.id,spriteIndex,atlas.id.id,color);
          }
          case SpriteType.Slice(border) => {
            FFISeijaUI.entityAddSpriteSlice(App.worldPtr,entity.id,spriteIndex,atlas.id.id,border,color)
          }
    }
}


given SpriteComponent:RawComponent[Sprite] with  {
  type BuilderType = SpriteBuilder;
  type RawType = Ptr[Byte]
  override def builder(): BuilderType = new SpriteBuilder()

  override def getRaw(entity: Entity): RawType = ???


}