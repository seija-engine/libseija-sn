package com.seija.ui.controls
import com.seija.core.reflect.ReflectType

class TabControl extends Selector derives ReflectType {
    var _selectedContent:Any = null
    def selectedContent:Any = this._selectedContent
    def selectedContent_=(value:Any):Unit = {
        this._selectedContent = value;callPropertyChanged("selectedContent")
    }

    var contentTemplate:Option[DataTemplate] = None


    override def Enter(): Unit = {
        super.Enter()
        if(this.hasItems && this._selectedItems._list.isEmpty) {
            this.selectIndex = 0;
        }
    }

    override def OnSelectionChanged(args: SelectionChangedEventArgs): Unit = {
        this.UpdateSelectedContent()
        super.OnSelectionChanged(args)
    }

    protected def UpdateSelectedContent():Unit = {
        val tabItem = this.GetSelectedTabItem()
        if(tabItem.isDefined) {
           this.selectedContent = tabItem.get.content;
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