package ui.controls
import ui.core._
import ui.binding.INotifyPropertyChanged
import ui.core.given;
import ui.BaseControl
import core.Entity
import transform.{Transform,given}
import ui.AtlasSprite
import core.IFromString
import ui.xml.IControlFromXml
import ui.Atlas
import math.Vector4

enum ImageType(val value:Int)  {
  case Simple extends ImageType(0)
  case Slice extends ImageType(1)
}

class Image extends BaseLayout with Cloneable  {
  protected var _sprite:Option[AtlasSprite] = None
  protected var _imageType:ImageType = ImageType.Simple
  protected var _color:Vector4 = Vector4.one

  def imageType = this._imageType
  def imageType_= (value:ImageType):Unit = { 
    this._imageType = value; 
    this.callPropertyChanged("imageType",this._imageType)
  }

  def sprite = this._sprite
  def sprite_= (value:Option[AtlasSprite]):Unit = { 
    this._sprite = value; 
    this.callPropertyChanged("sprite",this._sprite)
  }

  def color = this._color
  def color_=(value:Vector4) = {
    this._color = value;
    this.callPropertyChanged("color",this._color)
  }

  override def OnEnter(): Unit = {
    val spriteType = _imageType match
      case ImageType.Simple => ui.core.SpriteType.Simple
      case ImageType.Slice => {
        val border = this._sprite.flatMap(_.sliceInfo).getOrElse(Thickness.zero)
        ui.core.SpriteType.Slice(border)
      }
    
    val parentEntity = this.parent.flatMap(_.getEntity());
    val entity = Entity.spawnEmpty()
          .add[Transform](_.parent = parentEntity)
          .add[Rect2D]()
          .add[ItemLayout](v => {
            v.common.hor = this._hor;
            v.common.ver = this._ver;
            v.common.uiSize.width = this._width;
            v.common.uiSize.height = this._height;
          })
          .add[ui.core.Sprite](v => {
              if(this._sprite.isDefined) {
                v.atlas = Some(this._sprite.get.atlas.sheet);
                v.spriteIndex = this._sprite.get.index;
                v.typ = spriteType;
              }
              v.color = this._color;
          })
    this.entity = Some(entity)
  }

  override def onPropertyChanged(propertyName: String): Unit = {
    if(!this.isEntered) return;
    println(s"onPropertyChanged:${propertyName}")
    propertyName match
      case "sprite" => {
        this.entity.foreach(v => {
           val rawSprite = v.get[Sprite]();
           rawSprite.setSprite(this._sprite);
        })
      }
      case _ => 
  }

  override def clone():Image = {
    val control = super.clone().asInstanceOf[Image];
    
    control
  }
}

given IControlFromXml[Image] with {
    val name:String = "Image"
    def create():Image = new Image()
    def setStringPropery(control:Image,name:String,value:String):Unit = {
      given_IControlFromXml_BaseLayout.setStringPropery(control,name,value)
      name match
        case "sprite" => { 
          control.sprite = Atlas.getPath(value); 
          
        }
        case _ => {} 
    }
}

import core.reflect.*;
given ReflectType[Image] with {
  override def info: TypeInfo = TypeInfo("ui.controls.Image",() => Image(),Some(typeInfoOf[BaseLayout]),List(
     FieldInfo("sprite",
     (a,b) => a.asInstanceOf[Image].sprite = b.asInstanceOf[Option[AtlasSprite]],
     _.asInstanceOf[Image].sprite),
     FieldInfo("imageType",
     (a,b) => a.asInstanceOf[Image].imageType = b.asInstanceOf[ImageType],
     _.asInstanceOf[Image].imageType)
  ))
}