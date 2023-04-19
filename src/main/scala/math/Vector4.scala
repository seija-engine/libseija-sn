package math

import scala.scalanative.unsafe.{CStruct4,CFloat}
import core.RawFFI
import scala.scalanative.unsafe.Ptr

type RawVector4 = CStruct4[CFloat,CFloat,CFloat,CFloat]

final case class Vector4(val x:Float,val y:Float,val z:Float,val w:Float)


given Vector4RawFFI:RawFFI[Vector4,Ptr[RawVector4]] with {
    def toRaw(value: Vector4, ptr:Ptr[RawVector4]): Unit = {
        ptr._1 = value.x;
        ptr._2 = value.y;
        ptr._3 = value.z;
        ptr._4 = value.w;
    }
}

object Vector4 {
   final val one = Vector4(1,1,1,1)
}