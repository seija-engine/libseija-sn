package ui.controls

import ui.controls.UIElement
import scala.util.Try
import scala.util.Failure

class BaseTemplate extends Cloneable {
    def LoadContent(parent:UIElement):Try[UIElement] = { Failure(NotImplementedError()) }

    protected def setUIElementTemplate(curElement:UIElement,templateParent:UIElement):Unit = {
        curElement.templateParent = Some(templateParent);
        for(child <- curElement.children) {
            setUIElementTemplate(child,templateParent);
        }
    }
}