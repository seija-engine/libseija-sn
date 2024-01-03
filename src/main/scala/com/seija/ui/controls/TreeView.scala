package com.seija.ui.controls
import com.seija.core.reflect.ReflectType;
import com.seija.ui.controls.ItemsControl

class TreeView extends ItemsControl derives ReflectType {
    private var _selectedContainer:Option[TreeViewItem] = None;
    var _SelectedItem:Any = null;
    def SelectedItem:Any = this._SelectedItem
    def SelectedItem_=(value:Any):Unit = {
        this._SelectedItem = value;callPropertyChanged("SelectedItem")
    }


    def ChangeSelection(data:Any,container:TreeViewItem,selected:Boolean):Unit = {
        var changed:Boolean = false;
        val oldContainer:Option[TreeViewItem] = this._selectedContainer;
        var newValue:Any = null;
        var oldValue:Any = null;
        if(selected) {
           if(oldContainer.isEmpty || oldContainer.get != this._selectedContainer) {
            oldValue = SelectedItem;
            newValue = data;
            if(this._selectedContainer.isDefined) {
                this._selectedContainer.get.IsSelected = false;
                this._selectedContainer.get.UpdateContainsSelection(false);
            }
            this._selectedContainer = Some(container)
            container.UpdateContainsSelection(true)
            SetSelectedItem(data)
            changed = true;
           }
        } else {
            if(this._selectedContainer.isDefined && this._selectedContainer == container) {
                this._selectedContainer.get.UpdateContainsSelection(false);
                _selectedContainer = None;
                SetSelectedItem(null);
                oldValue = data;
                changed = true;
            }
        }
    }

    private def SetSelectedItem(data:Any):Unit = {
        if(this.SelectedItem != data) {
            this.SelectedItem = data;
        }
    }
}