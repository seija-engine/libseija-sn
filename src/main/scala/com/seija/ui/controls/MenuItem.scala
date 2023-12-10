package com.seija.ui.controls
import com.seija.core.reflect.ReflectType
import com.seija.ui.binding.NotifyCollectionChangedEventArgs
import com.seija.ui.event.EventManager
import com.seija.ui.event.EventType
import scalanative.unsigned.*
import com.seija.ui.visualState.ViewStates
import com.seija.ui.command.ICommand

enum MenuItemRole {
  case TopLevelItem
  case TopLevelHeader
  case SubmenuItem
  case SubmenuHeader
}

class MenuItem extends HeaderedItemsControl derives ReflectType {
    var command:Option[ICommand] = None
    var commandParams:Any = null

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
    }

    private var parentMenu:Option[Menu] = None
    private var parentMenuItem:Option[MenuItem] = None
    var isSelect:Boolean = false

    override def Awake(): Unit = {
      super.Awake()
    }
    override def OnEnter(): Unit = {
        this.logicParent.foreach {
          case item:MenuItem => this.parentMenuItem = Some(item)
          case menu:Menu => this.parentMenu = Some(menu)
          case _ =>
        }
        this.updateMenuRole()
        super.OnEnter()
        EventManager.register(this.getEntity().get,EventType.CLICK | EventType.ALL_MOUSE,false,OnElementEvent)
    }

    protected def OnElementEvent(typ:UInt,px:Float,py:Float,args:Any):Unit = {
      val zero = 0.toUInt
      val isClick = (typ & EventType.CLICK) != zero
      val isMouseEnter = (typ & EventType.MOUSE_ENTER) != zero
      val isMouseLeave = (typ & EventType.MOUSE_LEAVE) != zero
      if(isMouseLeave) this.IsHover = false
      if(isMouseEnter) this.IsHover = true
      
      this._role match
        case MenuItemRole.TopLevelItem | MenuItemRole.TopLevelHeader => {
          if(isClick || isMouseEnter || isMouseLeave) { this.parentMenu.foreach(_.onChildItemEvent(this,typ)) }
        }
        case MenuItemRole.SubmenuItem | MenuItemRole.SubmenuHeader => {
          if(isClick || isMouseEnter || isMouseLeave) { this.parentMenuItem.foreach(_.onChildItemEvent(this,typ)) }
        }
      
      if(isClick && (this._role == MenuItemRole.TopLevelItem || this.role == MenuItemRole.SubmenuItem)) {
        this.callCommand()
      }
    }

    private var selectItem:Option[MenuItem] = None
    
    def onChildItemEvent(childItem:MenuItem,typ:UInt):Unit = {
      val zero = 0.toUInt
      val isMouseEnter = (typ & EventType.MOUSE_ENTER) != zero
      val isMouseLeave = (typ & EventType.MOUSE_LEAVE) != zero

      if(isMouseEnter && childItem.IsHover) {
        this.selectItem.foreach {v =>
          v.setViewState(ViewStates.CommonStates,ViewStates.Normal)
          setMenuItemOpen(v,false)
        }
        childItem.setViewState(ViewStates.CommonStates,ViewStates.MouseOver)
        this.selectItem = Some(childItem)
        this.setMenuItemOpen(childItem,true)
      }
     
    }

    def hasHover():Boolean = {
      if(this._IsHover) return true
      val wrapPanel = this.itemsPresenter.map(_.children(0)).orNull
      if(wrapPanel != null) {
        for(child <- wrapPanel.children) {
          child match
            case childItem:MenuItem => if(childItem.hasHover()) return true
            case _ =>
          } 
      }
      false
    }
    

    private def setMenuItemOpen(item:MenuItem,isOpen:Boolean):Unit = {
      if(isOpen == false) {
        if(item.itemsPresenter.isDefined) {
          val wrapPanel = item.itemsPresenter.get.children(0)
          if(wrapPanel != null) {
            for(child <- wrapPanel.children) {
              child match
                case childItem:MenuItem => {
                  childItem.selectItem = None
                  this.setMenuItemOpen(childItem,false)
                }
                case _ =>
            }
          }
        }
        item.IsHover = false
        item.setViewState(ViewStates.CommonStates,ViewStates.Normal)
      }
      if(item.isSubmenuOpen != isOpen) {
        item.isSubmenuOpen = isOpen
      }
    }
    def closeALLItem():Unit = {
      val wrapPanel = itemsPresenter.map(_.children(0)).orNull
      if(wrapPanel != null) {
        for(child <- wrapPanel.children) {
           child match
             case childItem:MenuItem => { childItem.closeALLItem() }
             case _ =>
        }
      }
      if(this.IsHover == false || this.role == MenuItemRole.SubmenuItem || this.role == MenuItemRole.SubmenuHeader) {
        this.setViewState(ViewStates.CommonStates,ViewStates.Normal)
      }
      if(this.isSubmenuOpen) {
        this.isSubmenuOpen = false
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

    protected def callCommand():Unit = {
      this.command.foreach { cmd => cmd.Execute(this.commandParams); }
    }


    override def Exit(): Unit = {
        EventManager.unRegister(this.getEntity().get)
        super.Exit()
    }
}