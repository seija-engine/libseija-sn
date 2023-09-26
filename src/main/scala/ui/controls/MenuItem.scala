package ui.controls
import core.reflect.ReflectType
import ui.binding.NotifyCollectionChangedEventArgs
import ui.event.EventManager
import ui.event.EventType
import scalanative.unsigned.*

enum MenuItemRole {
  case TopLevelItem
  case TopLevelHeader
  case SubmenuItem
  case SubmenuHeader
}

class MenuItem extends HeaderedItemsControl derives ReflectType {
    protected var _isSubmenuOpen:Boolean = false
    def isSubmenuOpen:Boolean = this._isSubmenuOpen
    def isSubmenuOpen_=(value:Boolean):Unit = {
        this._isSubmenuOpen = value; callPropertyChanged("isSubmenuOpen",this)
    }
    protected var _role:MenuItemRole = MenuItemRole.TopLevelItem
    def role:MenuItemRole = this._role
    def role_=(value: MenuItemRole): Unit = {
      this._role = value
      callPropertyChanged("role",this)
      //slog.info(s"Role:${this._role}")
    }

    override def Awake(): Unit = {
      super.Awake()
    }
    override def OnEnter(): Unit = {
        this.updateMenuRole()
        super.OnEnter()
        EventManager.register(this.getEntity().get,EventType.CLICK,OnElementEvent)
    }

    protected def OnElementEvent(typ:UInt,px:Float,py:Float,args:Any):Unit = {
        val zero = 0.toUInt
        if(this.role == MenuItemRole.TopLevelHeader) {
          if((typ & EventType.CLICK) != zero) {
            if(!isSubmenuOpen) { isSubmenuOpen = true }
          }
        } else if(this.role == MenuItemRole.SubmenuHeader) {
          if((typ & EventType.CLICK) != zero) {
            if(!isSubmenuOpen) { isSubmenuOpen = true }
          }
        }
    }

    def updateMenuRole():Unit = {
      this.updateHasItems()
      val isParentMenu = this.getLogicParent.isDefined && this.getLogicParent.get.isInstanceOf[Menu]
      val menuRole = if(this.hasItems) {
        if(isParentMenu) MenuItemRole.TopLevelHeader  else MenuItemRole.SubmenuHeader
      } else {
        if(isParentMenu) MenuItemRole.TopLevelItem else MenuItemRole.SubmenuItem
      }
      this.role = menuRole
    }

    override def OnItemsChanged(args: NotifyCollectionChangedEventArgs): Unit = {
      super.OnItemsChanged(args)
      this.updateMenuRole()
    }

    override def Exit(): Unit = {
        EventManager.unRegister(this.getEntity().get)
        super.Exit()
    }
}