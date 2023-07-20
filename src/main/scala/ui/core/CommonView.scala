package ui.core
import core.RawFFI;
import scala.scalanative.unsafe._
import ui.core.Thickness
import core.formString
import core.reflect.Into
import scala.util.Try
import core.reflect.TypeCastException

type RawUISize = CStruct4[Byte, Byte, CFloat, CFloat]

enum SizeValue extends Cloneable {
  case Auto
  case FormRect
  case Pixel(v: Float)

  def getPixel():Option[Float] = {
    this match
      case Auto => None
      case FormRect => None
      case Pixel(v) => Some(v)
    
  }
}

object SizeValue {
  given Into[String,SizeValue] with {
    override def into(fromValue: String): SizeValue = fromValue match
      case "*" => SizeValue.Auto
      case "-" => SizeValue.FormRect
      case _ => SizeValue.Pixel(fromValue.toFloat)    
  }

  given Into[Float,SizeValue] with {
    override def into(fromValue: Float): SizeValue = SizeValue.Pixel(fromValue)
  }
}



enum LayoutAlignment(val v:Byte) {
  case Start extends LayoutAlignment(0)
  case Center extends LayoutAlignment(1)
  case End extends LayoutAlignment(2)
  case Stretch extends LayoutAlignment(3)
}

object LayoutAlignment {
  

    given Into[String,LayoutAlignment] with {
      override def into(fromValue: String): LayoutAlignment = fromValue match {
        case "Start" => LayoutAlignment.Start
        case "Center" => LayoutAlignment.Center
        case "End" => LayoutAlignment.End
        case "Stretch" => LayoutAlignment.Stretch
        case _: String => throw ClassCastException("LayoutAlignment");
      }
    }
}




enum Orientation(val v:Byte) {
    case Horizontal extends Orientation(0)
    case Vertical extends Orientation(1)
}

object Orientation {
  given Into[String,Orientation] with {
    override def into(fromValue: String): Orientation = fromValue match {
      case "Hor" => Orientation.Horizontal
      case "Ver" => Orientation.Vertical
      case _: String => throw TypeCastException("String","Orientation")
    }
  }
}



case class UISize(var width: SizeValue, var height: SizeValue)

object UISize {

  given Into[String,UISize] with {
    override def into(fromValue: String): UISize = {
        val values = fromValue.split("x");
        if(values.length == 2) {
            val width:SizeValue = formString[SizeValue](values(0)).get
            val height:SizeValue = formString[SizeValue](values(1)).get
            UISize(width,height)
        } else {
          throw TypeCastException("String","UISize")
        }
    }
  }
}



case class CommonView(
  var margin:Thickness = Thickness.zero,
  var padding:Thickness = Thickness.zero,
  var hor:LayoutAlignment = LayoutAlignment.Stretch,
  var ver:LayoutAlignment = LayoutAlignment.Stretch,
  var useRectSize:Boolean = false,
  var pixelPerfact:Boolean = true,
  var uiSize:UISize = UISize(SizeValue.Auto,SizeValue.Auto)
);

type RawCommonView = CStruct6[RawThickness,RawThickness,Byte,Byte,Boolean,Boolean]

given CommonViewToFFI:RawFFI[CommonView,Ptr[RawCommonView]] with {
    override def toRaw(value: CommonView, ptr: Ptr[RawCommonView]): Unit = {
    ThicknessRawFFI.toRaw(value.margin,ptr.at1)
    ThicknessRawFFI.toRaw(value.padding,ptr.at2)
    ptr._3 = value.hor.v;
    ptr._4 = value.ver.v;
    ptr._5 = value.useRectSize;
    ptr._6 = value.pixelPerfact;
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
