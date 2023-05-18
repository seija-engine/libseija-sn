package ui.controls
import core.reflect.Assembly
import math.Color
import scala.annotation.experimental;
import scala.annotation.meta.setter
import ui.binding.{INotifyPropertyChanged, BProp}
import ui.binding.autoProp
import ui.core.Text as CoreText;
import core.reflect._;
import ui.Font
import core.{formString,IFromString};
import core.Entity
import transform.Transform
import ui.core.Rect2D
import ui.core.ItemLayout
class Text extends BaseLayout with Cloneable derives ReflectType {
  var _text: String = "Text"
  def text = this._text;
  def text_=(value: String) = {
    this._text = value; this.callPropertyChanged("text", this);
  }

  var _color: Color = Color.black;
  def color = this._color;
  def color_=(value: Color) = {
    this._color = value; this.callPropertyChanged("color", this);
  }

  var _font:Font = Font.getDefault_?()
  def font = this._font;
  def font_=(font:Font) = {
    this._font = font; this.callPropertyChanged("font", this);
  }

  var _fontSize:Int = 24
  def fontSize = this._fontSize;
  def fontSize_=(value:Int) = {
    this._fontSize = value; this.callPropertyChanged("fontSize",this);
  }

  override def OnEnter(): Unit = {
    val parentEntity = this.parent.flatMap(_.getEntity());
    println(s"text parentEntity: ${parentEntity}")
    val entity = Entity.spawnEmpty()
          .add[Transform](_.parent = parentEntity)
          .add[Rect2D]()
          .add[ItemLayout](v => {
            v.common.hor = this._hor;
            v.common.ver = this._ver;
            v.common.uiSize.width = this._width;
            v.common.uiSize.height = this._height;
          })
          .add[CoreText](v => {
              v.text = this._text;
              v.fontSize = this._fontSize;
              v.color = this._color.toVector4();
              v.font = this._font.handle;
          })
    this.entity = Some(entity);
  }

  override def onPropertyChanged(propertyName: String): Unit = {
    propertyName match
      case "text" => {
      }
      case _: String => 
    
  }
};
