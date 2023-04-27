package ui.controls
import ui.core._
import ui.INotifyPropertyChanged
import ui.core.given;
import ui.BaseControl
import core.Entity
import transform.{Transform,given}
import ui.AtlasSprite
import core.IStringPropObject

class Image extends BaseControl with INotifyPropertyChanged  {

  private var _hor:LayoutAlignment = LayoutAlignment.Stretch
  protected  var _ver:LayoutAlignment = LayoutAlignment.Stretch
  protected  var _width:SizeValue = SizeValue.Auto
  protected  var _height:SizeValue = SizeValue.Auto
  protected  var _sprite:Option[AtlasSprite] = None


  def width_= (value:SizeValue):Unit = { 
    this._width = value; 
    this.callPropertyChanged("width")
  }

  def height_= (value:SizeValue):Unit = { 
    this._height = value; 
    this.callPropertyChanged("height")
  }

  def hor = this._hor
  def hor_=(value:LayoutAlignment):Unit = { 
    this._hor = value;
    this.callPropertyChanged("hor") 
  }

 
  def ver_=(value:LayoutAlignment):Unit = { 
    this._ver = value;
    this.callPropertyChanged("ver") 
  }

  def sprite = this._sprite
  def sprite_= (value:Option[AtlasSprite]):Unit = { 
    this._sprite = value; 
    this.callPropertyChanged("sprite")
  }

  override def OnEnter(): Unit = {
    this._hor = LayoutAlignment.Stretch;
    val parentEntity = this.parent.flatMap(_.getEntity());
    Entity.spawnEmpty()
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
              }
          })
  }

  override def onPropertyChanged(propertyName: String): Unit = {
    if(!this.isEntered) return;
    propertyName match
      case "sprite" => 
      case _ => 
  }
}


given ImageFormString:IStringPropObject[Image] with {

  override def setProperty(target: Image, name: String, value: String): Unit = {
    /*
    name match
      case "hor" => target.hor = LayoutAlignment.fromString(value)
      case "ver" => target.ver = LayoutAlignment.fromString(value)
      case "width" => target.width = SizeValue.fromString(value)
      case "height" => target.height = SizeValue.fromString(value)
      case "sprite" => target.sprite = AtlasSprite.fromString(value)
      case _ =>*/
  }

  def create():Image = new Image();

  
}