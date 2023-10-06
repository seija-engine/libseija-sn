package ui.controls
import core.reflect.ReflectType

class TabControl extends Selector derives ReflectType {
    var _selectedContent:Any = null
    def selectedContent:Any = this._selectedContent
    def selectedContent_=(value:Any):Unit = {
        this._selectedContent = value;callPropertyChanged("selectedContent",this)
    }

    var contentTemplate:Option[DataTemplate] = None
}