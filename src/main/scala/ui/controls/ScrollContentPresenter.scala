package ui.controls
import core.reflect.*
import core.Entity
import ui.core.FreeLayout;
import ui.core.FreeLayoutItem;
class ScrollContentPresenter extends UIElement with IScrollInfo derives ReflectType {
    private var scrollInfo:Option[IScrollInfo] = None
    var scrollViewer: Option[ScrollViewer] = None
    var content:Any = null;
    var childEntity:Option[Entity] = None;
    override def OnEnter():Unit = {
        val entity:Entity = this.createBaseEntity(false);
        this.enterScroll();
        entity.add[FreeLayout](v => {
            v.common.hor = this._hor;
            v.common.ver = this._ver;
            v.common.uiSize.width = this._width;
            v.common.uiSize.height = this._height;
            v.common.padding = this._padding;
            v.common.margin = this._margin;
        });
    }

    override def Enter(): Unit = {
        super.Enter()
        val childElement = this.children.head;
        childElement.getEntity().foreach {childEntity =>
           childEntity.add[FreeLayoutItem]();
        };
        this.childEntity = childElement.getEntity();

    }

    def enterScroll():Unit = {
        if(this.templateParent.isEmpty || !this.templateParent.get.isInstanceOf[ScrollViewer]) {
            return;
        }
        val scrollView = this.templateParent.get.asInstanceOf[ScrollViewer];
        this.scrollInfo = Some(this);
        this.scrollViewer = Some(scrollView);
        if(this.content == null) { this.content = scrollView.content; }
        if(this.content != null) {
            this.content match {
                case uiElement:UIElement => {
                    this.addChild(uiElement);
                }
            }
        }
    }

    override def SetHorizontalOffset(offset: Float): Unit = {
        val rawLayoutItem = this.childEntity.get.get[FreeLayoutItem]();
        rawLayoutItem._1 = offset;
    }

    override def SetVerticalOffset(offset: Float): Unit = {
        val rawLayoutItem = this.childEntity.get.get[FreeLayoutItem]();
        rawLayoutItem._2 = offset;
    }

}