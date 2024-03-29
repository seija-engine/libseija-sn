package com.seija.math
import com.seija.core.reflect.Into

case class Color(val r: Float, val g: Float, val b: Float, val a: Float) {
  def toVector4(): Vector4 = Vector4(r, g, b, a)

  def toVector3:Vector3 = Vector3(r,g,b)
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
    val a = if(curValue.length() == 9) {
      Integer.parseInt(curValue.substring(7, 9), 16)
    } else { 255 }
    Some(Color(r / 255f, g / 255f, b / 255f, a / 255f))
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
