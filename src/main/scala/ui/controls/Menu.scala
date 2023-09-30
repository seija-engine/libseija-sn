package ui.controls
import core.reflect.*
import ui.core.Orientation
import scalanative.unsigned.*
import ui.event.EventType
import ui.visualState.ViewStates

class Menu extends ItemsControl derives ReflectType {
  override protected def defaultWrapPanel: Panel = {
    val stack = StackPanel()
    stack.orientation = Orientation.Horizontal
    stack
  }

  private var selectItem:Option[MenuItem] = None

  def onChildItemEvent(item:MenuItem, typ:UInt):Unit = {
    val zero = 0.toUInt
    val isMouseEnter = (typ & EventType.MOUSE_ENTER) != zero
    val isMouseLeave = (typ & EventType.MOUSE_LEAVE) != zero
    val isClick = (typ & EventType.CLICK) != zero
    
    if(isClick) {
      this.setSelectItem(item)
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

  def setSelectItem(item:MenuItem):Unit = {
    this.selectItem.foreach {v => v.isSubmenuOpen = false; }
    item.isSubmenuOpen = true
    this.selectItem = Some(item)
  }

}
