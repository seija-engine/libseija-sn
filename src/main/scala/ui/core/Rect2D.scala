package ui.core

import core.RawComponent
import core.Entity
import core.RawComponentBuilder
import math.Vector4
import ui.core.FFISeijaUI


class Rect2D

class Rect2DBuilder extends RawComponentBuilder {
  var width:Float = 100;
  var height:Float = 100;
  var anchorX:Float = 0.5;
  var anchorY:Float = 0.5;
  override def build(entity: Entity): Unit = {
    FFISeijaUI.entityAddRect(core.App.worldPtr,entity.id,Vector4(width,height,anchorX,anchorY));
  }

    
}

object Rect2D {
  given Rect2dComponent:RawComponent[Rect2D] with {
    type BuilderType = Rect2DBuilder;
    type RawType = Rect2D;

    override def builder(): BuilderType = new Rect2DBuilder()

    override def getRaw(entity: Entity): RawType = ???
   
  }
}

