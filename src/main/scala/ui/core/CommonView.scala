package ui.core
import core.RawFFI;
import scala.scalanative.unsafe._
import ui.core.Thickness

type RawUISize = CStruct4[Byte, Byte, CFloat, CFloat]

enum SizeValue {
  case Auto
  case FormRect
  case Pixel(v: Float)
}

enum LayoutAlignment(val v:Byte) {
  case Start extends LayoutAlignment(0)
  case Center extends LayoutAlignment(1)
  case End extends LayoutAlignment(2)
  case Stretch extends LayoutAlignment(3)
}

enum Orientation(val v:Byte) {
    case Horizontal extends Orientation(0)
    case Vertical extends Orientation(1)
}

case class UISize(var width: SizeValue, var height: SizeValue)

case class CommonView(
  var margin:Thickness = Thickness.zero,
  var padding:Thickness = Thickness.zero,
  var hor:LayoutAlignment = LayoutAlignment.Stretch,
  var ver:LayoutAlignment = LayoutAlignment.Stretch,
  var useRectSize:Boolean = false,
  var uiSize:UISize = UISize(SizeValue.Auto,SizeValue.Auto)
);

type RawCommonView = CStruct5[RawThickness,RawThickness,Byte,Byte,Boolean]

given CommonViewToFFI:RawFFI[CommonView,Ptr[RawCommonView]] with {
    override def toRaw(value: CommonView, ptr: Ptr[RawCommonView]): Unit = {
    ThicknessRawFFI.toRaw(value.margin,ptr.at1)
    ThicknessRawFFI.toRaw(value.padding,ptr.at2)
    ptr._3 = value.hor.v;
    ptr._4 = value.ver.v;
    ptr._5 = value.useRectSize;
  }
}

given SizeValueToFFI: RawFFI[UISize, Ptr[RawUISize]] with {
  override def toRaw(value: UISize, ptr: Ptr[RawUISize]): Unit = {
    ptr._3 = 0;
    ptr._4 = 0;
    value.width match {
      case SizeValue.Auto     => ptr._1 = 0;
      case SizeValue.FormRect => ptr._1 = 1;
      case SizeValue.Pixel(v) => {
        ptr._1 = 2;
        ptr._3 = v;
      }
    }
    value.height match
        case SizeValue.Auto => ptr._2 = 0;
        case SizeValue.FormRect => ptr._2 = 1;
        case SizeValue.Pixel(v) => {
            ptr._2 = 2;
            ptr._4 = v;
        }
  }
}
