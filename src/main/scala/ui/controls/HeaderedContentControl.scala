package ui.controls
import core.reflect.ReflectType

class HeaderedContentControl extends ContentControl derives ReflectType {
    var _header:Any = _
    var _headerTemplate:Option[DataTemplate] = None

    def header:Any = this._header
    def header_=(value:Any):Unit = {
        this._header = value; callPropertyChanged("header",this)
    }
    def headerTemplate:DataTemplate = this._headerTemplate.get
    def headerTemplate_=(value:DataTemplate): Unit = {
        this._headerTemplate = Some(value);callPropertyChanged("headerTemplate",this)
    }
}