package ui.core
import _root_.core.RawFFI
import core.reflect.{Into,TypeCastException};
import scala.scalanative.unsafe.{CFloat,CStruct4}
import scala.scalanative.unsafe.Ptr
import core.reflect.DynTypeConv
type RawThickness = CStruct4[CFloat, CFloat, CFloat, CFloat];

case class Thickness(val left:Float,val top:Float,val right:Float,val bottom:Float);

object Thickness {
    def apply(v:Float):Thickness = Thickness(v,v,v,v)
    final val zero = Thickness(0,0,0,0)

    given Into[String, Thickness] with {
        override def into(value: String): Thickness = {
            val parts = value.split(",");
            if (parts.length == 4) {
                val left = parts(0).toFloatOption;
                val top = parts(1).toFloatOption;
                val right = parts(2).toFloatOption;
                val bottom = parts(3).toFloatOption;
                if (left.isDefined && top.isDefined && right.isDefined && bottom.isDefined) {
                   return Thickness(left.get,top.get,right.get,bottom.get)
                }
            }
            throw new TypeCastException("String","Thickness")
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

enum CommonViewStates(val v:Byte) {
    case Normal extends CommonViewStates(0)
    case Hover  extends CommonViewStates(1)
    case Pressed extends CommonViewStates(2)
    case Disabled extends CommonViewStates(3)
}

object CommonViewStates {
    given Into[String,CommonViewStates] with {
        override def into(fromValue: String): CommonViewStates = {
            fromValue match {
                case "Normal" => CommonViewStates.Normal
                case "Hover" => CommonViewStates.Hover
                case "Pressed" => CommonViewStates.Pressed
                case "Disabled" => CommonViewStates.Disabled
                case _ => throw TypeCastException("String","CommonViewStates")
            }
        }
    }
}