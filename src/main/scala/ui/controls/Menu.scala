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

  def onChildItemEvent(item:MenuItem, typ:UInt):Unit = {
    val zero = 0.toUInt
    val isMouseEnter = (typ & EventType.MOUSE_ENTER) != zero
    val isMouseLeave = (typ & EventType.MOUSE_LEAVE) != zero
    if(isMouseEnter) {
      item.setViewState(ViewStates.CommonStates,ViewStates.MouseOver)
    }
    if(isMouseLeave) {
      item.setViewState(ViewStates.CommonStates,ViewStates.Normal)
    }
  }
}
