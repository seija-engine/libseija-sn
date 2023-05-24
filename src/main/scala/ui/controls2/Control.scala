package ui.controls2
import ui.controls2.template.ControlTemplate

class Control extends UIElement {
    var template:Option[ControlTemplate] = None
    override def Enter(): Unit = {
        super.Enter()
    }
}