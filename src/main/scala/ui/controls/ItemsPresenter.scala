package ui.controls
import core.logError
import core.reflect.*

class ItemsPresenter extends UIElement derives ReflectType {
    var template:Option[ItemsPanelTemplate] = None

    protected var lstMgr:Option[ItemElementListMgr] = None;

    def getItemsControl:Option[ItemsControl] = this.templateParent match {
      case Some(value: ItemsControl) => Some(value)
      case _ => None
    }

    override def OnEnter(): Unit = {
      this.createBaseEntity(true)
      val itemsControl = this.getItemsControl
      if(itemsControl.isEmpty) {
        slog.error(s"ItemsPresenter collection or parent is empty ${this.templateParent}")
        return 
      }
      this.attachToOwner(itemsControl.get)
      this.applyTemplate()
      //val warpPanel = itemsControl.get.getWarpPanel
      //this.addChild(warpPanel)
      //this.lstMgr = Some(ItemElementListMgr(warpPanel,itemsControl.get.itemCollection))
      //this.lstMgr.get.start()
    }

    def attachToOwner(itemsOwner:ItemsControl):Unit = {
      this.template = itemsOwner.itemsPanel
    }

    def applyTemplate():Unit = {
      this.template match 
        case Some(t) => t.LoadContent(this,None).logError().foreach{ e =>
          this.addChild(e)
        }
        case None => slog.warn("ItemsPresenter template is nil")
    }
}