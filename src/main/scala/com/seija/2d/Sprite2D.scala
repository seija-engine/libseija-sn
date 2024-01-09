package com.seija.`2d`

import com.seija.core.{RawComponentBuilder,RawComponent}
import com.seija.core.Entity
import com.seija.asset.Handle
import com.seija.ui.core.SpriteSheet
import com.seija.math.Color

class Sprite2D;

class Sprite2DBuilder extends RawComponentBuilder {
    var atlas:Option[Handle[SpriteSheet]] = None;
    var color:Color = Color.white;
    var spriteIndex:Int = -1;
    override def build(entity: Entity): Unit = {
        val atlasIndex:Long = atlas.map(_.id.id).getOrElse(0L);
        FFISeija2D.addSprite2D(com.seija.core.App.worldPtr,atlasIndex,entity,spriteIndex,color.toVector4())
    }
}

object Sprite2D {
    given Sprite2DComponent:RawComponent[Sprite2D] with  {
        type BuilderType = Sprite2DBuilder;
        type RawType = Unit
        override def builder(): BuilderType = new Sprite2DBuilder()
        override def getRaw(entity: Entity,isMut:Boolean): RawType = ()
    }
}