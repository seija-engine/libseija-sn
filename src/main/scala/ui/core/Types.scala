package ui.core
import _root_.core.RawFFI
import scala.scalanative.unsafe.{CFloat,CStruct4}
import scala.scalanative.unsafe.Ptr
type RawThickness = CStruct4[CFloat, CFloat, CFloat, CFloat];

case class Thickness(val left:Float,top:Float,right:Float,bottom:Float) {
    
}

object Thickness {
    def apply(v:Float):Thickness = Thickness(v,v,v,v)
    final val zero = Thickness(0,0,0,0)
}

given ThicknessRawFFI:RawFFI[Thickness,Ptr[RawThickness]] with {
    def toRaw(value: Thickness, ptr:Ptr[RawThickness]): Unit = {
        ptr._1 = value.left;
        ptr._2 = value.top;
        ptr._3 = value.right;
        ptr._4 = value.bottom;
    }
}