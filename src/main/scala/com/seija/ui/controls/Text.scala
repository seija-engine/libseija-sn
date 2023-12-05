package com.seija.ui.controls
import com.seija.ui.core.Text as CoreText;
import com.seija.core.reflect.*;
import com.seija.math.Color
import com.seija.ui.Font
import com.seija.ui.ContentProperty
import com.seija.ui.core.AnchorAlign

@ContentProperty("text")
class Text extends UIElement derives ReflectType {
  var _text: String = "Text"
  var _color: Color = Color.black;
  var _font: Option[Font] = Font.getDefault();
  var _fontSize: Int = 24
  var _anchor: AnchorAlign = AnchorAlign.Center;

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
  def anchor = this._fontSize;
  def anchor_=(value: AnchorAlign) = {
    this._anchor = value; this.callPropertyChanged("anchor", this);
  }
  
  override def OnEnter(): Unit = {
    val newEntity = this.createBaseEntity(true);
    if(this.font.isDefined) {
        newEntity.add[CoreText](v => {
            v.text = this._text;
            v.fontSize = this._fontSize;
            v.color = this._color.toVector4();
            v.font = this._font.get.handle;
            v.anchor = this._anchor;
        })
    }
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
      case "color" => {
        this.entity.foreach(e => {
           val rawText = e.get[CoreText]();
           rawText.setColor(this._color);
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
