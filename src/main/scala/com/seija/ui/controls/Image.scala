package com.seija.ui.controls
import com.seija.ui.{AtlasSprite, LayoutUtils}
import com.seija.math.Color
import com.seija.ui.core.Thickness
import com.seija.ui.core.SpriteType
import com.seija.core.Entity
import com.seija.transform.Transform
import com.seija.ui.core.Rect2D
import com.seija.ui.core.ItemLayout
import com.seija.ui.core.Sprite
import com.seija.core.reflect.{Into, TypeCastException,ReflectType}
import com.seija.core.Time

enum ImageType(val value:Int)  {
  case Simple extends ImageType(0)
  case Slice extends ImageType(1)
}

object ImageType {
  given Into[String,ImageType] with {
    override def into(fromValue: String): ImageType = fromValue match {
      case "Simple" => ImageType.Simple
      case "Slice" => ImageType.Slice
      case _: String => throw TypeCastException("String","ImageType")
    }
  }
}

class Image extends UIElement derives ReflectType {
    var _sprite:Option[AtlasSprite] = None
    var _imageType:ImageType = ImageType.Simple
    var _color:Color = Color.white;

    def imageType = this._imageType
    def imageType_= (value:ImageType):Unit = { this._imageType = value; this.callPropertyChanged("imageType") }

    def sprite = this._sprite
    def sprite_= (value:Option[AtlasSprite]):Unit = { this._sprite = value; this.callPropertyChanged("sprite") }

    def color = this._color
    def color_=(value:Color) = { this._color = value; this.callPropertyChanged("color") }

    override def OnEnter(): Unit = {
      val spriteType = this.getSpriteType;
      val parentEntity = this.parent.flatMap(_.getEntity());
      //println(s"Image.OnEnter() ${this._sprite.get.name} ${parentEntity} ${this.parent}")
      val entity = Entity.spawnEmpty()
          .add[Transform](_.parent = parentEntity)
          .add[Rect2D]()
          .add[ItemLayout](v => {
            v.common.hor = this._hor;
            v.common.ver = this._ver;
            v.common.uiSize.width = this._width;
            v.common.uiSize.height = this._height;
            v.common.margin = this._margin;
            v.common.padding = this._padding;
          })
          .add[Sprite](v => {
              if(this._sprite.isDefined) {
                v.atlas = Some(this._sprite.get.atlas.sheet);
                v.spriteIndex = this._sprite.get.index;
                v.typ = spriteType;
              }
              v.color = this._color;
          })
      this.addEntityStateInfo(entity)
      this.entity = Some(entity)
    }

    private def getSpriteType:SpriteType = _imageType match {
      case ImageType.Simple => com.seija.ui.core.SpriteType.Simple
      case ImageType.Slice => { 
        val border = this._sprite.flatMap(_.sliceInfo).getOrElse(Thickness.zero) 
         com.seija.ui.core.SpriteType.Slice(border)
      }
    }

    override def onPropertyChanged(propertyName: String): Unit = {
      super.onPropertyChanged(propertyName);
      propertyName match
        case "sprite" => {
          this.entity.foreach(v => {
             val rawSprite = v.get[Sprite]();
             rawSprite.setSprite(this._sprite);
          })
        }
        case "color" => {
          this.entity.foreach {v =>
            val rawSprite = v.get[Sprite]();
            rawSprite.setColor(this._color);
          }
        }
        case "width" => {
          val frame = com.seija.core.Time.getFrameCount()
          this.entity.foreach {v =>
            if(LayoutUtils.isInPostLayout) {
              LayoutUtils.addPostLayoutDirtyEntity(v)
            }
            val rawLayout = v.get[ItemLayout]();
            rawLayout.setWidth(this._width);
          }
        }
        case "height" => {
          this.entity.foreach {v =>
            if (LayoutUtils.isInPostLayout) {
              LayoutUtils.addPostLayoutDirtyEntity(v)
            }
            val rawLayout = v.get[ItemLayout]();
            rawLayout.setHeight(this._height);
          }
        }
        case _ => 
  }

}