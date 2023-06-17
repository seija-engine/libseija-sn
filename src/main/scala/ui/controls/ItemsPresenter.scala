package ui.controls
import core.reflect.*;

class ItemsPresenter extends UIElement derives ReflectType {
    protected var lstMgr:Option[ItemElementListMgr] = None;
    override def Awake(): Unit = {
        super.Awake();
    }

    def getItemsControl():Option[ItemsControl] = {
        if(this.templateParent.isEmpty) return None;
        if(this.templateParent.get.isInstanceOf[ItemsControl]) {
            return Some(this.templateParent.get.asInstanceOf[ItemsControl]);
        }
        None
    }

    override def OnEnter(): Unit = {
      this.createBaseEntity(true);
      val itemsControl = this.getItemsControl();
      println("ItemsPresenter Enter");
      if(itemsControl.isEmpty) {
        System.err.println("ItemsPresenter collection or parent is empty");
        return 
      };
      this.addChild(itemsControl.get.warpElement);
      this.lstMgr = Some(ItemElementListMgr(itemsControl.get.warpElement,itemsControl.get.itemCollection));
      this.lstMgr.get.start();
    }
}