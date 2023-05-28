package ui.controls
import ui.controls.ControlTemplate
import core.reflect.*;

class Control extends UIElement derives ReflectType {
    var template:Option[ControlTemplate] = None
}