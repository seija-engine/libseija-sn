package com.seija.ui.core

import com.seija.core.RawComponent
import com.seija.core.Entity
import com.seija.core.RawComponentBuilder
import com.seija.math.{RawVector4, Vector3, Vector4}
import com.seija.ui.core.FFISeijaUI

import scala.scalanative.unsafe.Ptr
import com.seija.math.Rect2D


class Rect2D

class Rect2DBuilder extends RawComponentBuilder {
  var width:Float = 100;
  var height:Float = 100;
  var anchorX:Float = 0.5;
  var anchorY:Float = 0.5;
  override def build(entity: Entity): Unit = {
    FFISeijaUI.entityAddRect(com.seija.core.App.worldPtr,entity.id,Vector4(width,height,anchorX,anchorY));
  }

    
}

object Rect2D {
  given Rect2dComponent:RawComponent[Rect2D] with {
    type BuilderType = Rect2DBuilder;
    type RawType = RawRect2D

    override def builder(): BuilderType = new Rect2DBuilder()

    override def getRaw(entity: Entity,isMut:Boolean): RawType = {
      RawRect2D(FFISeijaUI.entityGetRect2d(com.seija.core.App.worldPtr,entity.id,isMut))
    }
   
  }
}

case class RawRect2D(ptr: Ptr[RawVector4]) extends AnyVal {
  def width:Float = ptr._1
  def height:Float = ptr._2
  def anchorX:Float = ptr._3
  def anchorY:Float = ptr._4

  def toData:com.seija.math.Rect2D = com.seija.math.Rect2D(width, height, anchorX, anchorY)
}