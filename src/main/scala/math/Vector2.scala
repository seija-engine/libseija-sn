package math
import scala.scalanative.unsafe._

type RawVector2 = CStruct2[CFloat,CFloat]

case class Vector2(val x:Float,val y:Float) {
    def +(other:Vector3):Vector2 = Vector2(x + other.x,y + other.y)
}

object Vector2 {
    val zero:Vector2 = Vector2(0,0)
}