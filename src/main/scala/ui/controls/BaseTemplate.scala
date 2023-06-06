package ui.controls

import ui.controls.UIElement
import scala.util.Try
import scala.util.Failure

class BaseTemplate extends Cloneable {
    def LoadContent(parent:UIElement):Try[UIElement] = { Failure(NotImplementedError()) }  
}