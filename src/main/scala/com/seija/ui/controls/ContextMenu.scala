package com.seija.ui.controls
import com.seija.core.reflect.ReflectType

class ContextMenu extends ItemsControl derives ReflectType {
    var _isOpen:Boolean = false
    def isOpen:Boolean = this._isOpen
    def isOpen_=(value:Boolean):Unit = {
        this._isOpen = value;callPropertyChanged("isOpen")
    }
    
}