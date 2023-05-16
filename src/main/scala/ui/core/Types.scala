package ui.core
import _root_.core.RawFFI
import core.IFromString;
import scala.scalanative.unsafe.{CFloat,CStruct4}
import scala.scalanative.unsafe.Ptr
type RawThickness = CStruct4[CFloat, CFloat, CFloat, CFloat];

case class Thickness(val left:Float,top:Float,right:Float,bottom:Float) {
    
}

object Thickness {
    def apply(v:Float):Thickness = Thickness(v,v,v,v)
    final val zero = Thickness(0,0,0,0)

    given IFromString[Thickness] with {
        def from(value:String):Option[Thickness] = {
            val parts = value.split(",");
            if (parts.length == 4) {
                val left = parts(0).toFloatOption;
                val top = parts(1).toFloatOption;
                val right = parts(2).toFloatOption;
                val bottom = parts(3).toFloatOption;
                if (left.isDefined && top.isDefined && right.isDefined && bottom.isDefined) {
                    Some(Thickness(left.get,top.get,right.get,bottom.get))
                } else {
                    None
                }
            } else {
                None
            }
        }
    }
}

given ThicknessRawFFI:RawFFI[Thickness,Ptr[RawThickness]] with {
    def toRaw(value: Thickness, ptr:Ptr[RawThickness]): Unit = {
        ptr._1 = value.left;
        ptr._2 = value.top;
        ptr._3 = value.right;
        ptr._4 = value.bottom;
    }
}