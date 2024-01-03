package com.seija.ui.controls
import com.seija.core.reflect.ReflectType;

class HeaderedItemsControl extends ItemsControl derives ReflectType {
    var _header:Any = _
    var _headerTemplate:Option[DataTemplate] = None

    //region Setter

    def header:Any = this._header
    def header_=(value:Any):Unit = {
        this._header = value; callPropertyChanged("header")
    }
    def headerTemplate:DataTemplate = this._headerTemplate.get
    def headerTemplate_=(value:DataTemplate): Unit = {
        this._headerTemplate = Some(value);callPropertyChanged("headerTemplate")
    }
    //endregion
    
    def PrepareHeaderedItemsControl(item:Any,parentItemsControl:ItemsControl):Unit = {
        
    }

}