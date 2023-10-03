package ui.controls
import core.reflect.*
import ui.core.Orientation
import scalanative.unsigned.*
import ui.event.EventType
import ui.visualState.ViewStates
import ui.event.EventManager
import core.UpdateMgr
import input.Input
import input.KeyCode
import input.MouseButton
import core.Time

class Menu extends ItemsControl derives ReflectType {
  override protected def defaultWrapPanel: Panel = {
    val stack = StackPanel()
    stack.orientation = Orientation.Horizontal
    stack
  }
  
  private var waitCloseFrame:Long = 0
  override def OnEnter(): Unit = {
    super.OnEnter()
    UpdateMgr.add(this.onUpdate)
  }

  private var selectItem:Option[MenuItem] = None

  def onChildItemEvent(item:MenuItem, typ:UInt):Unit = {
    val zero = 0.toUInt
    val isMouseEnter = (typ & EventType.MOUSE_ENTER) != zero
    val isMouseLeave = (typ & EventType.MOUSE_LEAVE) != zero
    val isClick = (typ & EventType.CLICK) != zero
   
    
    if(isClick) {
      if(this.selectItem.isEmpty) {
        this.setSelectItem(item)
      }
    }                             
    
    this.selectItem match
      case None => {
        if(isMouseEnter) {
          item.setViewState(ViewStates.CommonStates,ViewStates.MouseOver)
        }
        if(isMouseLeave) {
          item.setViewState(ViewStates.CommonStates,ViewStates.Normal)
        }
      }
      case Some(value) => {
        if(isMouseEnter) {
          if(item != value) {
            value.setViewState(ViewStates.CommonStates,ViewStates.Normal)
            item.setViewState(ViewStates.CommonStates,ViewStates.MouseOver)
            this.setSelectItem(item)
          }
        }
      }
  }

  def closeALLMenu():Unit = {
    val wrapPanel = this.getWarpPanel
    if(wrapPanel != null) {
      for(child <- wrapPanel.children) {
        child match
          case item:MenuItem => item.closeALLItem()
          case _ =>
      }
    }
    this.selectItem = None
  }

  def setSelectItem(item:MenuItem):Unit = {
    this.selectItem.foreach {v => v.closeALLItem() }
    item.isSubmenuOpen = true
    this.selectItem = Some(item)
  }

  def onUpdate(dt:Float):Unit = {
    if(this.selectItem.isDefined ) {
      if(Input.getMouseUp(MouseButton.Left) || Input.getMouseUp(MouseButton.Right)) {
        this.waitCloseFrame = Time.Frame + 1
      }
    }
    if(this.waitCloseFrame == Time.Frame) {
      this.closeALLMenu()
    }
  }

}
