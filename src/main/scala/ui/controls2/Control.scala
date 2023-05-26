package ui.controls2
import ui.controls2.template.ControlTemplate
import core.reflect.*;

class Control extends UIElement derives ReflectType {
    var template:Option[ControlTemplate] = None
}