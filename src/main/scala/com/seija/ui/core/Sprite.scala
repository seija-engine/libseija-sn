package com.seija.ui.core

import com.seija.asset.Handle
import com.seija.core.{App, Entity, RawComponent, RawComponentBuilder}
import com.seija.math.Color
import com.seija.ui.AtlasSprite

import scala.scalanative.unsafe.Ptr

class Sprite;

case class RawSprite(val rawPtr:Ptr[Byte]) {
  def setSprite(sprite:Option[AtlasSprite]) = {
    sprite match {
      case Some(s) => {
        FFISeijaUI.spriteSetSptite(App.worldPtr,this.rawPtr,s.index, s.atlas.sheet.id.id)
      }
      case None => {
        FFISeijaUI.spriteSetSptite(App.worldPtr,this.rawPtr,-1,0)
      }
    }
  }

  def setColor(color:Color):Unit = {
    FFISeijaUI.spriteSetColor(rawPtr,color);
  }
}

enum SpriteType {
  case Simple
  case Slice(border:Thickness)
}

class SpriteBuilder extends RawComponentBuilder {
    var spriteIndex:Int = -1;
    var atlas:Option[Handle[SpriteSheet]] = None;
    var color:Color = Color.white;
    var typ:SpriteType = SpriteType.Simple
    def build(entity: Entity): Unit = {
        val atlasIndex:Long = atlas.map(_.id.id).getOrElse(0L);
        typ match
          case SpriteType.Simple => {
            FFISeijaUI.entityAddSpriteSimple(App.worldPtr,entity.id,spriteIndex,atlasIndex,this.color.toVector4());
          }
          case SpriteType.Slice(border) => {
            FFISeijaUI.entityAddSpriteSlice(App.worldPtr,entity.id,spriteIndex,atlasIndex,border,this.color.toVector4())
          }
    }
}

object Sprite {
    given SpriteComponent:RawComponent[Sprite] with  {
     type BuilderType = SpriteBuilder;
     type RawType = RawSprite
     override def builder(): BuilderType = new SpriteBuilder()

     override def getRaw(entity: Entity,isMut:Boolean): RawType =
       RawSprite(FFISeijaUI.entityGetSprite(App.worldPtr,entity.id,isMut))
   }
}

