package ui.controls
import core.reflect.Assembly
import math.Color
import ui.binding.AutoChangedGetSetter
import scala.annotation.experimental;
import scala.annotation.meta.setter


class Text extends BaseLayout with Cloneable {
    var _text:String = "";
    var _color:Color = Color.white;
    
}

object Text {
    def aa() = {
        val text = new Text();
    }
}

