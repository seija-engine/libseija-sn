package ui.controls
import core.reflect.*

class ItemsPresenter extends UIElement derives ReflectType {
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
      val warpPanel = itemsControl.get.getWarpPanel
      this.addChild(warpPanel)
      this.lstMgr = Some(ItemElementListMgr(warpPanel,itemsControl.get.itemCollection))
      this.lstMgr.get.start()
    }
}