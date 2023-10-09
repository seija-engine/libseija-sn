package ui.controls

import ui.binding.INotifyCollectionChanged
import ui.binding.NotifyCollectionChangedEventArgs
import ui.binding.CollectionChangedAction

trait IGeneratorHost {
    def View:ItemCollection = null
    def GetContainerForItem(itemData:Any):UIElement
    def IsItemItsOwnContainer(itemData:Any):Boolean

    def PrepareItemContainer(container:UIElement,itemData:Any):Unit
}

case class ItemContainerGenerator(host:IGeneratorHost) {
    private var curIndex:Int = -1

    host.View.changedCallBack = Some(this.OnCollectionChanged)

    def StartAt(index:Int):Unit = {
        this.curIndex = index
    }

    def GenerateNext():Option[UIElement] = {
        val dataList = host.View.getDataList
        if(this.curIndex >= dataList.length) {  return None; }
        val itemData = dataList(this.curIndex)
        val container:UIElement = this.host.GetContainerForItem(itemData)
        this.linkContainerToItem(container,itemData)
        this.curIndex += 1
        Some(container)
    }

    def linkContainerToItem(container:UIElement,itemData:Any):Unit = {
        container.dataContext = itemData
        container._ItemForItemContainer = itemData
    }

    def PrepareItemContainer(container:UIElement):Unit = {
        val itemData = container._ItemForItemContainer
        this.host.PrepareItemContainer(container,itemData)
    }

    protected def OnCollectionChanged(sender:INotifyCollectionChanged,args:NotifyCollectionChangedEventArgs):Unit = {
        args.action match
            case CollectionChangedAction.Add => OnItemAdded(args.newItem,args.newStartingIndex)
            case CollectionChangedAction.Remove =>
            case CollectionChangedAction.Replace =>
            case CollectionChangedAction.Move =>
            case CollectionChangedAction.Clear =>
        
    }

    def OnItemAdded(item:Any,index:Int):Unit = {
        
    }
}