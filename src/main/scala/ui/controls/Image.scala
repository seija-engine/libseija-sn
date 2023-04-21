package ui.controls
import ui.core._
import ui.INotifyPropertyChanged
import ui.core.given;
import ui.BaseControl
import core.Entity
import transform.{Transform,given}
import ui.AtlasSprite

class Image extends BaseControl with INotifyPropertyChanged {
  private var _hor:LayoutAlignment = LayoutAlignment.Stretch
  protected  var ver:LayoutAlignment = LayoutAlignment.Stretch
  protected  var width:SizeValue = SizeValue.Auto
  protected  var height:SizeValue = SizeValue.Auto
  protected  var _sprite:Option[AtlasSprite] = None

  def hor = this._hor
  def hor_=(value:LayoutAlignment):Unit = { 
    this._hor = value;
    this.callPropertyChanged("hor") 
  }

  def sprite = this._sprite
  def sprite_= (value:Option[AtlasSprite]):Unit = { 
    this._sprite = value; 
    this.callPropertyChanged("sprite")
  }

  override def OnEnter(): Unit = {
    this.hor = LayoutAlignment.Stretch;
    val parentEntity = this.parent.flatMap(_.getEntity());
    Entity.spawnEmpty()
          .add[Transform](_.parent = parentEntity)
          .add[Rect2D]()
          .add[ItemLayout](v => {
            v.common.hor = this._hor;
            v.common.ver = this.ver;
            v.common.uiSize.width = this.width;
            v.common.uiSize.height = this.height;
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
