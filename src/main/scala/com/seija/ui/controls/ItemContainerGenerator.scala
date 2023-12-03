package com.seija.ui.controls

import com.seija.ui.binding.INotifyCollectionChanged
import com.seija.ui.binding.NotifyCollectionChangedEventArgs
import com.seija.ui.binding.CollectionChangedAction
import scala.collection.mutable

case class ItemsChangedEventArgs(action:CollectionChangedAction,index:Int,count:Int,oldIndex:Int = -1)

type ItemsChangedEventHandler = (sender:Any,args:ItemsChangedEventArgs) => Unit

trait IGeneratorHost {
    def View:ItemCollection = null
    def GetContainerForItem(itemData:Any):UIElement
    def IsItemItsOwnContainer(itemData:Any):Boolean

    def PrepareItemContainer(container:UIElement,itemData:Any):Unit
}

case class ItemContainerGenerator(host:IGeneratorHost) {
    private var curIndex:Int = -1
    var ItemsChanged:Option[ItemsChangedEventHandler] = None
    val itemInfos:mutable.HashMap[Int,ItemInfo] = mutable.HashMap.empty

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
        this.itemInfos.update(this.curIndex,ItemInfo(itemData,Some(container),this.curIndex))
        this.curIndex += 1
        this.itemInfos.update(this.curIndex,ItemInfo(itemData,Some(container),this.curIndex))
        Some(container)
    }

    def linkContainerToItem(container:UIElement,itemData:Any):Unit = {
        if(container != itemData) {
            container.dataContext = itemData
        }
        container.SetPropValue(ItemContainerGenerator.ItemForItemContainer,itemData)
    }

    def PrepareItemContainer(container:UIElement):Unit = {
        val itemData = container.GetPropValue(ItemContainerGenerator.ItemForItemContainer)
        this.host.PrepareItemContainer(container,itemData)
    }

    protected def OnCollectionChanged(sender:INotifyCollectionChanged,args:NotifyCollectionChangedEventArgs):Unit = {
        args.action match
            case CollectionChangedAction.Add => OnItemAdded(args.newItem,args.newStartingIndex)
            case CollectionChangedAction.Remove => {
                this.ItemsChanged.foreach(_(this,ItemsChangedEventArgs(args.action,args.oldStartingIndex,1)))   
            }
            case CollectionChangedAction.Replace => {
                this.ItemsChanged.foreach(_(this,ItemsChangedEventArgs(args.action,args.newStartingIndex,1)))
            }
            case CollectionChangedAction.Move => {
                this.ItemsChanged.foreach(_(this,ItemsChangedEventArgs(args.action,args.newStartingIndex,1,args.oldStartingIndex)))
            }
            case CollectionChangedAction.Clear => {
                this.ItemsChanged.foreach(_(this,ItemsChangedEventArgs(args.action,0,0)))
                this.itemInfos.clear()   
            }
        
    }

    def OnItemAdded(item:Any,index:Int):Unit = {
        this.ItemsChanged.foreach(_(this,ItemsChangedEventArgs(CollectionChangedAction.Add,index,1)))   
    }

    def ItemFromContainer(element:UIElement):Any = {
       var itemData = element.GetPropValue(ItemContainerGenerator.ItemForItemContainer)
       itemData
    }

    def IndexFromContainer(container:UIElement):Int = {
        -1
    }

    def IndexFromItemData(itemData:Any):Int = host.View.getDataList.indexOf(itemData)

    def ContainerFromIndex(index:Int):Option[UIElement] = {
        this.itemInfos.get(index).flatMap(_.container)
    }
}

object ItemContainerGenerator {
    val ItemForItemContainer:PropertyDefine = PropertyDefine("ItemContainerGenerator.ItemForItemContainer",null)
}