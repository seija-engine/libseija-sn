package com.seija.ui.controls
import com.seija.core.reflect.ReflectType;
import com.seija.ui.visualState.ViewStates
import com.seija.ui.event.EventManager
import com.seija.ui.event.EventType
import scalanative.unsigned.*
class TreeViewItem extends HeaderedItemsControl derives ReflectType {
    private var ContainsSelection:Boolean = false;

    var _IsExpanded:Boolean = false;
    def IsExpanded:Boolean = this._IsExpanded
    def IsExpanded(value:Boolean):Unit = {
        this._IsExpanded = value; callPropertyChanged("IsExpanded")
    }

    protected var _IsSelected:Boolean = false;
    def IsSelected:Boolean = this._IsSelected
    def IsSelected_=(value:Boolean) = {
        this._IsSelected = value; callPropertyChanged("IsSelected")
    }

    protected def ParentTreeView:Option[TreeView] = { 
        var curItems = this.ParentItemsControl;
        while(curItems.isDefined) {
            curItems match
                case Some(value:TreeView) => return Some(value) 
                case Some(value) => curItems = ItemsControl.ItemsControlFromItemContainer(value);
                case _ => 
        }
        None    
    }

    protected def ParentItemsControl:Option[ItemsControl] = ItemsControl.ItemsControlFromItemContainer(this)
    protected def ParentTreeViewItem:Option[TreeViewItem] = ParentItemsControl match
        case Some(value:TreeViewItem) => Some(value)
        case _ => None
    
    private def headerElement:Option[UIElement] = this.nameDict.get("PART_Header")

    override def Enter(): Unit = {
        this.setViewState(ViewStates.SelectionStates,ViewStates.Unselected)
        super.Enter();
        this.headerElement.foreach {header =>
            EventManager.register(header.getEntity().get,EventType.ALL_TOUCH,false,true,this.OnElementEvent)
        }
        this.updateVisualState()
    }

    def OnElementEvent(typ:UInt,mouse:UInt,px:Float,py:Float,args:Any):Unit = {
        this.processViewStates(typ,args);
        val zero = 0.toUInt
        if((typ & EventType.TOUCH_START) != zero) {
            if(!this._IsSelected) {
                this.Select(true)
            }
        }
    }

    private def Select(selected:Boolean):Unit = {
        val treeView = this.ParentTreeView
        val parent = this.ParentItemsControl
        if(treeView.isDefined && parent.isDefined) {
           val itemData = parent.get.GetItemOrContainerFromContainer(this);
           treeView.get.ChangeSelection(itemData,this,selected)
        }
    }

    override def onPropertyChanged(propertyName: String): Unit = {
        propertyName match
            case "IsExpanded" => { this.updateVisualState() }
            case "IsSelected" => { this.updateVisualState() }
            case _ =>
    }

    override def updateVisualState(): Unit = {
        this._IsActive = this._IsSelected;
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

    def UpdateContainsSelection(selected:Boolean):Unit = {
        var parent = this.ParentTreeViewItem
        while(parent.isDefined) {
            parent.get.ContainsSelection = selected;
            parent = parent.get.ParentTreeViewItem
        }
    }

    override def GetContainerForItemOverride(): UIElement = new TreeViewItem()

    override def Exit(): Unit = {
        this.headerElement.foreach {header =>
            EventManager.unRegister(header.getEntity().get)
        }
        super.Exit()
    }
}
