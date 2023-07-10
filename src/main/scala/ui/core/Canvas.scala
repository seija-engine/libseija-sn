package ui.core

import core.RawComponentBuilder
import core.Entity
import core.RawComponent
import scala.scalanative.unsafe.Ptr

class Canvas;

class CanvasBuilder extends RawComponentBuilder {
    var isClip:Boolean = false;
    def build(entity: Entity): Unit = {
        FFISeijaUI.entityAddCanvas(core.App.worldPtr,entity.id,isClip)
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