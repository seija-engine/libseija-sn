package ui.controls
import core.reflect.*
import core.Entity
import ui.LayoutUtils
import ui.core.{FreeLayout, FreeLayoutItem, Rect2D}
import ui.event.{RouteEvent, RouteEventArgs}
class ScrollContentPresenter extends UIElement derives ReflectType {
  var childEntity: Option[Entity] = None

  override def OnEnter(): Unit = {
    val entity: Entity = this.createBaseEntity(false);
    entity.add[FreeLayout](v => {
      v.common.hor = this._hor;
      v.common.ver = this._ver;
      v.common.uiSize.width = this._width;
      v.common.uiSize.height = this._height;
      v.common.padding = this._padding;
      v.common.margin = this._margin;
    })
    if (
      this.templateParent.isDefined && this.templateParent.get
        .isInstanceOf[ScrollViewer]
    ) {
      val scrollView = this.templateParent.get.asInstanceOf[ScrollViewer];
      scrollView.hookContentPresenter(this);
    }
    LayoutUtils.addPostLayout(this.onPostLayout)
  }

  override def Enter(): Unit = {
    super.Enter()
    val childElement = this.children.head
    childElement.getEntity().foreach { childEntity =>
      childEntity.add[FreeLayoutItem]();
    }
    this.childEntity = childElement.getEntity()
  }

  def setVerticalOffset(value:Float):Unit = this.childEntity.foreach { entity =>
     val freeItem = entity.get[FreeLayoutItem](true)
     freeItem._2 = -value
     if(LayoutUtils.postLayoutStep >= 0) {
        LayoutUtils.addPostLayoutDirtyEntity(entity)
     }
  }
  
 

  def setHorizontalOffset(value:Float):Unit = {

  }

  protected def onPostLayout(step: Int): Unit = {
    if (LayoutUtils.isDirty(this.childEntity.get, step)) {
      val childRect = this.childEntity.get.get[Rect2D]()
      this.routeEventController.fireEvent(ScrollSizeChangedEvent(false,childRect.width,childRect.height))
    }
    if (LayoutUtils.isDirty(this.entity.get, step)) {
      val thisRect = this.entity.get.get[Rect2D]()
      this.routeEventController.fireEvent(ScrollSizeChangedEvent(true, thisRect.width, thisRect.height))
    }
  }

  override def Exit(): Unit = {
    super.Exit()
    LayoutUtils.removePostLayout(this.onPostLayout)
  }
}

class ScrollSizeChangedEvent(val isViewport:Boolean,val width:Float, val height:Float) extends RouteEventArgs(ScrollContentPresenter.ContentChanged,false)
object ScrollContentPresenter {
  val ContentChanged:RouteEvent = RouteEvent("ContentChanged",classOf[ScrollContentPresenter])
}