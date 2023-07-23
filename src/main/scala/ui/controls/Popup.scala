package ui.controls
import ui.controls.UIElement
import core.reflect.ReflectType
import ui.ContentProperty
import core.Entity
import transform.Transform
import ui.core.{FreeLayoutItem, ItemLayout, Rect2D}

@ContentProperty("child")
class Popup extends UIElement derives ReflectType {
  var _child:UIElement = UIElement.zero

  def child:UIElement = this._child
  def child_=(value:UIElement):Unit = {
    this._child = value; callPropertyChanged("child",this)
  }

  override def OnEnter(): Unit = {
    val topEntity = ui.CanvasManager.popup().getEntity().get
    val newEntity = Entity.spawnEmpty()
                          .add[Transform](_.parent = Some(topEntity) )
                          .add[Rect2D]()
    this.addEntityStateInfo(newEntity)
    newEntity.add[ItemLayout](v => {
                v.common.hor = this._hor;
                v.common.ver = this._ver;
                v.common.uiSize.width = this._width;
                v.common.uiSize.height = this._height;
                v.common.padding = this._padding;
                v.common.margin = this._margin;
            })
    newEntity.add[FreeLayoutItem]()
    this.entity = Some(newEntity)
    this.addChild(this.cloneChild())
  }

  protected def cloneChild():UIElement = this._child.clone()
} 