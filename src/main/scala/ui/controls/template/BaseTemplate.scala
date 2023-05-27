package ui.controls.template

import ui.controls.UIElement
import scala.util.Try
import scala.util.Failure

class BaseTemplate {
    def LoadContent():Try[UIElement] = { Failure(NotImplementedError()) }  
}