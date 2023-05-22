package math
import core.reflect.Into

case class Color(val r: Float, val g: Float, val b: Float, val a: Float) {
  def toVector4(): Vector4 = Vector4(r, g, b, a)
}

object Color {

  val white = Color(1, 1, 1, 1);
  val black = Color(0, 0, 0, 1)

 

  def formHex(strValue: String): Option[Color] = {
    var curValue = strValue;

    if (strValue.length() == 4) {
      var hexNew = "#";
      for (idx <- 1 until strValue.length()) {
        hexNew += strValue.charAt(idx);
        hexNew += strValue.charAt(idx);
      }
      curValue = hexNew;
    }

    val r = Integer.parseInt(curValue.substring(1, 3), 16);
    val g = Integer.parseInt(curValue.substring(3, 5), 16);
    val b = Integer.parseInt(curValue.substring(5, 7), 16);
    Some(Color(r / 255f, g / 255f, b / 255f, 1))
  }

  given Into[String,Color] with {
    override def into(fromValue: String): Color = {
      if (fromValue.startsWith("rgb(")) {
        
      } else if (fromValue.startsWith("rgba(")) {
       
      } else if (fromValue.startsWith("#")) {
        return Color.formHex(fromValue).getOrElse(throw new Exception(s"${fromValue} to color err"));
      }
      ???
    }
  }
}
