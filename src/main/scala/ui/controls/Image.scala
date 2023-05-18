package ui.controls
import ui.core._
import ui.binding.INotifyPropertyChanged
import ui.core.given;
import ui.BaseControl
import core.Entity
import transform.{Transform,given}
import ui.AtlasSprite
import core.IFromString
import ui.Atlas
import math.Vector4
import math.Color
import core.reflect.ReflectType
import core.formString

enum ImageType(val value:Int)  {
  case Simple extends ImageType(0)
  case Slice extends ImageType(1)
}

object ImageType {
  given IFromString[ImageType] with {

    override def from(strValue: String): Option[ImageType] = strValue match {
      case "Simple" => Some(ImageType.Simple)
      case "Slice" => Some(ImageType.Slice)
      case _: String => None
    }
  }
}

class Image extends BaseLayout with Cloneable derives ReflectType {
  var _sprite:Option[AtlasSprite] = None
  var _imageType:ImageType = ImageType.Simple
  var _color:Color = Color.white;

  def imageType = this._imageType
  def imageType_= (value:ImageType):Unit = { 
    this._imageType = value; 
    this.callPropertyChanged("imageType",this)
  }

  def sprite = this._sprite
  def sprite_= (value:Option[AtlasSprite]):Unit = { 
    this._sprite = value; 
    this.callPropertyChanged("sprite",this)
  }

  def color = this._color
  def color_=(value:Color) = {
    this._color = value;
    this.callPropertyChanged("color",this)
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
