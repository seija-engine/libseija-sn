package ui
import core.RawFFI
import scala.scalanative.unsafe.{CFloat,CStruct4}
type RawThickness = CStruct4[CFloat, CFloat, CFloat, CFloat];

case class Thickness(val left:Float,top:Float,right:Float,bottom:Float) {
    
}

given thicknessRawFFI:RawFFI[Thickness,CStruct4[CFloat,CFloat,CFloat,CFloat]] with {
    def toRaw(value: Thickness, ptr: CStruct4[CFloat, CFloat, CFloat, CFloat]): Unit = {
        ptr._1 = value.left;
        ptr._2 = value.top;
        ptr._3 = value.right;
        ptr._4 = value.bottom;
    }
}