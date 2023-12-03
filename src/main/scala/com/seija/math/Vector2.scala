package com.seija.math
import scala.scalanative.unsafe._

type RawVector2 = CStruct2[CFloat,CFloat]

case class Vector2(var x:Float,var y:Float) extends Cloneable {
    def +(other:Vector3):Vector2 = Vector2(x + other.x,y + other.y)

    override def clone():Vector2 = Vector2(x,y)
}

object Vector2 {
    val zero:Vector2 = Vector2(0,0)
}