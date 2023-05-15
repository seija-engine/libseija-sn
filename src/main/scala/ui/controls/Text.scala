package ui.controls
import core.reflect.Assembly
import math.Color
import scala.annotation.experimental;
import scala.annotation.meta.setter
import ui.binding.{INotifyPropertyChanged, BProp}
import ui.binding.autoProp
import ui.core.Text as CoreText;
import core.reflect._;

class Text extends BaseLayout with Cloneable derives ReflectType {
  var _text: String = "Text"
  def text = this._text;
  def text_=(value: String) = {
    this._text = value; this.callPropertyChanged("text", this);
  }

  var _color: Color = Color.white;
  def color = this._color;
  def color_=(value: Color) = {
    
    this._color = value; this.callPropertyChanged("color", this);
  }

  override def OnEnter(): Unit = {}
};

