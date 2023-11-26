package ui.controls
import core.reflect.ReflectType

class TabControl extends Selector derives ReflectType {
    var _selectedContent:Any = null
    def selectedContent:Any = this._selectedContent
    def selectedContent_=(value:Any):Unit = {
        this._selectedContent = value;callPropertyChanged("selectedContent",this)
    }

    var contentTemplate:Option[DataTemplate] = None

    override def OnSelectionChanged(args: SelectionChangedEventArgs): Unit = {
        this.UpdateSelectedContent()
        super.OnSelectionChanged(args)
    }

    protected def UpdateSelectedContent():Unit = {
        val tabItem = this.GetSelectedTabItem()
        if(tabItem.isDefined) {
           val selectedContent = tabItem.get.content;
           
        }
    }

    private def GetSelectedTabItem():Option[TabItem] = {
        val selectItem = this.SelectedItem
        val tabItem = selectItem.asInstanceOf[TabItem]
        if(tabItem != null) {
            Some(tabItem)   
        } else {
            None
        }
    }
}