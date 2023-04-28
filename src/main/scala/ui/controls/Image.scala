package ui.controls
import ui.core._
import ui.INotifyPropertyChanged
import ui.core.given;
import ui.BaseControl
import core.Entity
import transform.{Transform,given}
import ui.AtlasSprite
import core.IFromString
import ui.xml.IControlFromXml

class Image extends BaseLayout  {
  protected  var _sprite:Option[AtlasSprite] = None
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

given IControlFromXml[Image] with {
    val name:String = "Image"
    def create():Image = new Image()
    def setStringPropery(control:Image,name:String,value:String):Unit = {
      given_IControlFromXml_BaseLayout.setStringPropery(control,name,value)
    }

}