package math
import core.IFromString;

case class Color(val r:Float,val g:Float,val b:Float,val a:Float);


given IFromString[Color] with {
   def from(strValue:String):Option[Color] = {
      if(strValue.startsWith("#")) {
        
      }
      None
   } 
}