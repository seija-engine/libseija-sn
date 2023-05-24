package ui.controls2.template

import ui.controls2.UIElement
import scala.util.Try
import scala.util.Failure

class BaseTemplate {
    def LoadContent():Try[UIElement] = { Failure(NotImplementedError()) }  
}