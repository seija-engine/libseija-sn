package ui.controls
import ui.core.Text as CoreText;
import core.reflect.*;
import math.Color
import ui.Font
import ui.ContentProperty

@ContentProperty("text")
class Text extends UIElement derives ReflectType {
  var _text: String = "Text"
  var _color: Color = Color.black;
  var _font: Option[Font] = Font.getDefault();
  var _fontSize: Int = 24

  def text = this._text;
  def text_=(value: String) = {
    this._text = value; this.callPropertyChanged("text", this);
  }
  def color = this._color;
  def color_=(value: Color) = {
    this._color = value; this.callPropertyChanged("color", this);
  }
  def font = this._font;
  def font_=(font: Option[Font]) = {
    this._font = font; this.callPropertyChanged("font", this);
  }  
  def fontSize = this._fontSize;
  def fontSize_=(value: Int) = {
    this._fontSize = value; this.callPropertyChanged("fontSize", this);
  }
  override def OnEnter(): Unit = {
    val newEntity = this.createBaseEntity(true);
    if(this.font.isDefined) {
        newEntity.add[CoreText](v => {
            v.text = this._text;
            v.fontSize = this._fontSize;
            v.color = this._color.toVector4();
            v.font = this._font.get.handle;
        })
    }
    println(s"add text:${this.text}")
  }

  override def onPropertyChanged(propertyName: String): Unit = {
    super.onPropertyChanged(propertyName);
    propertyName match
      case "text" => {
        this.entity.foreach(e => {
           val rawText = e.get[CoreText]();
           rawText.setText(this._text);
        })
      }
      case _: String => 
  }

  override def clone():Text = {
    val cloneObject = super.clone().asInstanceOf[Text];
    cloneObject._text = new String(this._text);
    cloneObject  
  }

   
}
