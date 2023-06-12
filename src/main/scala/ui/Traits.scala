package ui

import ui.controls.UIElement

trait ElementNameScope {
    def getScopeElement(name:String):Option[UIElement];
}

trait IAwake {
    def Awake():Unit;
}