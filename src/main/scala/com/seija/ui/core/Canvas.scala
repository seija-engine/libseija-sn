package com.seija.ui.core

import com.seija.core.RawComponentBuilder
import com.seija.core.Entity
import com.seija.core.RawComponent
import scala.scalanative.unsafe.Ptr

class Canvas;

class CanvasBuilder extends RawComponentBuilder {
    var isClip:Boolean = false;
    def build(entity: Entity): Unit = {
        FFISeijaUI.entityAddCanvas(com.seija.core.App.worldPtr,entity.id,isClip)
    }
}

object Canvas {
    given CanvasComponent:RawComponent[Canvas] with {
        type BuilderType = CanvasBuilder;
        type RawType = Ptr[Byte]

        override def builder(): BuilderType = new CanvasBuilder()

        override def getRaw(entity: Entity,isMut:Boolean): RawType = ???
    }
}