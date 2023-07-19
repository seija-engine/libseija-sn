package ui.controls
import core.reflect.*

class ItemsPresenter extends UIElement derives ReflectType {
    protected var lstMgr:Option[ItemElementListMgr] = None;
    override def Awake(): Unit = {
        super.Awake();
    }

    def getItemsControl:Option[ItemsControl] = this.templateParent match {
      case Some(value: ItemsControl) => Some(value)
      case _ => None
    }

    override def OnEnter(): Unit = {
      this.createBaseEntity(true);
      val itemsControl = this.getItemsControl;
      println("ItemsPresenter Enter");
      if(itemsControl.isEmpty) {
        System.err.println("ItemsPresenter collection or parent is empty");
        return 
      };
      val warpPanel = itemsControl.get.getWarpPanel
      this.addChild(warpPanel);
      this.lstMgr = Some(ItemElementListMgr(warpPanel,itemsControl.get.itemCollection));
      this.lstMgr.get.start();
    }
}