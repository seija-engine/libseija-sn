package com.seija.ui.controls
import com.seija.core.reflect.ReflectType;
import com.seija.ui.visualState.ViewStates

class TreeViewItem extends HeaderedItemsControl derives ReflectType {
    var _IsExpanded:Boolean = false;
    def IsExpanded:Boolean = this._IsExpanded
    def IsExpanded(value:Boolean):Unit = {
        this._IsExpanded = value; callPropertyChanged("IsExpanded")
    }

    override def onPropertyChanged(propertyName: String): Unit = {
        propertyName match
            case "IsExpanded" => { this.updateVisualState() }
            case _ =>
    }

    override def updateVisualState(): Unit = {
        super.updateVisualState()
        if(this._IsExpanded) {
            this.setViewState(ViewStates.ExpansionStates,ViewStates.Expanded)
        } else {
            this.setViewState(ViewStates.ExpansionStates,ViewStates.Collapsed)
        }
        if(this._hasItems) {
            this.setViewState(ViewStates.HasItemsStates,ViewStates.HasItems)
        } else {
            this.setViewState(ViewStates.HasItemsStates,ViewStates.NoItems)
        }
    }
}