package ui.core

import core.RawComponent
import core.Entity
import core.RawComponentBuilder
import math.{RawVector4, Vector3, Vector4}
import ui.core.FFISeijaUI

import scala.scalanative.unsafe.Ptr


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
    type RawType = RawRect2D

    override def builder(): BuilderType = new Rect2DBuilder()

    override def getRaw(entity: Entity,isMut:Boolean): RawType = {
      RawRect2D(FFISeijaUI.entityGetRect2d(core.App.worldPtr,entity.id,isMut))
    }
   
  }
}

case class RawRect2D(ptr: Ptr[RawVector4]) extends AnyVal {
  def width:Float = ptr._1
  def height:Float = ptr._2
  def anchorX:Float = ptr._3
  def anchorY:Float = ptr._4

  def toData:math.Rect2D = math.Rect2D(width, height, anchorX, anchorY)
}