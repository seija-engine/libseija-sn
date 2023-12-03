package com.seija.ui

import com.seija.ui.controls.UIElement

trait ElementNameScope {
    def getScopeElement(name:String):Option[UIElement];
    def setScopeElement(name:String,elem:UIElement):Unit = {}
}

trait IPostReader {
    def OnPostRead():Unit;
}