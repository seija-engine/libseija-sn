package ui.controls
import core.reflect.*
import ui.core.Orientation

class Menu extends ItemsControl derives ReflectType {
  override protected def defaultWrapPanel: Panel = {
    val stack = StackPanel()
    stack.orientation = Orientation.Horizontal
    stack
  }
}
