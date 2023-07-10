package ui.controls
import core.reflect.*
import core.Entity
import ui.core.FreeLayout;
import ui.core.FreeLayoutItem;
class ScrollContentPresenter extends UIElement derives ReflectType {
    private var scrollInfo:Option[IScrollInfo] = None
    var childEntity:Option[Entity] = None;
    override def OnEnter():Unit = {
        val entity:Entity = this.createBaseEntity(false);
        entity.add[FreeLayout](v => {
            v.common.hor = this._hor;
            v.common.ver = this._ver;
            v.common.uiSize.width = this._width;
            v.common.uiSize.height = this._height;
            v.common.padding = this._padding;
            v.common.margin = this._margin;
        })
        if(this.templateParent.isDefined && this.templateParent.get.isInstanceOf[ScrollViewer]) {
          val scrollView = this.templateParent.get.asInstanceOf[ScrollViewer];
          scrollView.hookContentPresenter(this);
        }
    }

    override def Enter():Unit = {
        super.Enter()
        val childElement = this.children.head
        childElement.getEntity().foreach { childEntity =>
           childEntity.add[FreeLayoutItem]();
        }
        this.childEntity = childElement.getEntity()
    }



}