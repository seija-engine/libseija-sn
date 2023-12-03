package com.seija.ui.controls
import com.seija.core.reflect.*
import scala.collection.mutable.ArrayBuffer
import scala.collection.IndexedSeq
import com.seija.ui.ContentProperty
import com.seija.core.logError
import com.seija.ui.binding.{INotifyCollectionChanged, NotifyCollectionChangedEventArgs}
import scala.util.Try
import scala.util.Failure
import scala.util.Success
import com.seija.ui.core.SizeValue
import com.seija.ui.resources.Style

@ContentProperty("items")
class ItemsControl extends Control with IGeneratorHost derives ReflectType {
    var items:ArrayBuffer[Any] = ArrayBuffer.empty
    protected var _itemsSource:IndexedSeq[Any] = null
    var itemTemplate:Option[DataTemplate] = None;
    
    var itemsPresenter:Option[ItemsPresenter] = None

    var _ItemContainerStyle:Option[Style] = None;
    def ItemContainerStyle:Option[Style] = this._ItemContainerStyle
    def ItemContainerStyle_=(value:Option[Style]):Unit = {
      this._ItemContainerStyle = value;callPropertyChanged("ItemContainerStyle",this)
    }
    
    var itemCollection:ItemCollection = ItemCollection()
    var itemGenerator:ItemContainerGenerator = ItemContainerGenerator(this)

    override def View: ItemCollection = this.itemCollection

    var itemsPanel:Option[ItemsPanelTemplate] = None

    protected var _hasItems:Boolean = false
    def hasItems: Boolean = this._hasItems
    def hasItems_=(value:Boolean):Boolean = {
      this._hasItems = value; callPropertyChanged("hasItems",this);
      this._hasItems
    }

    def itemsSource:IndexedSeq[Any] = this._itemsSource
    def itemsSource_=(value:IndexedSeq[Any]):Unit = {
      //移除旧的监听
      if (this._itemsSource != null && this._itemsSource.isInstanceOf[INotifyCollectionChanged]) {
        val notifyList = this._itemsSource.asInstanceOf[INotifyCollectionChanged]
        notifyList.removeChangedHandler(this.onItemsChangedHandle)
      }
      this._itemsSource = value; callPropertyChanged("itemsSource",this)
    }

    override def Awake(): Unit = {
      super.Awake()
      this.items.foreach {
        case v:UIElement => v.Awake()
        case _ =>
      }
      this.updateHasItems()
    }

    override def onPropertyChanged(propertyName: String): Unit = {
      super.onPropertyChanged(propertyName)
      propertyName match
        case "itemsSource" => {
          //添加新的监听
          if (this._itemsSource != null && this._itemsSource.isInstanceOf[INotifyCollectionChanged]) {
            val notifyList = this._itemsSource.asInstanceOf[INotifyCollectionChanged]
            notifyList.addChangedHandler(this.onItemsChangedHandle)
          }
        }
        case _ =>
    }
    override def OnEnter(): Unit = {
      this.updateHasItems()
      if(this.items.nonEmpty) {
        this.itemCollection.setItemSource(items)
      }
      if(this.itemsSource != null) {
        this.itemCollection.setItemSource(this.itemsSource)
      }
     
      super.OnEnter()
    }

    def updateHasItems():Unit = {
      var hasItems = false
      if(this.items.nonEmpty) hasItems = true
      if(this.itemsSource != null && this.itemsSource.nonEmpty) hasItems = true
      if(this._hasItems != hasItems) {
        this.hasItems = hasItems
      }
    }

    def onItemsChangedHandle(_sender:INotifyCollectionChanged, _args:NotifyCollectionChangedEventArgs):Unit = {
      this.updateHasItems()
      this.OnItemsChanged(_args)
    }

    def OnItemsChanged(args:NotifyCollectionChangedEventArgs):Unit = {}

    override def IsItemItsOwnContainer(itemData:Any):Boolean = { itemData.isInstanceOf[UIElement] }

    def GetContainerForItemOverride():UIElement = { 
      val element = new ContentPresenter()
      element
    }

    override def GetContainerForItem(itemData: Any): UIElement = {
      val container = if(IsItemItsOwnContainer(itemData)) {
        itemData.asInstanceOf[UIElement]
      } else {
        this.GetContainerForItemOverride()
      }
      container.setLogicParent(Some(this))
      container
    }

    override def PrepareItemContainer(container: UIElement, itemData: Any): Unit = {
      this._ItemContainerStyle.foreach {style =>
        this.applyStyle(style,container)
      }
      container match
        case cp:ContentPresenter => {
          cp.PrepareContentPresenter(itemData,this.itemTemplate)
        }
        case ip:ItemsPresenter => {

        }
        case _ =>
    }

    def GetItemOrContainerFromContainer(container:UIElement):Any = {
      var itemData = this.itemGenerator.ItemFromContainer(container)
      if(itemData == null && this.IsItemItsOwnContainer(container)) {
        itemData = container
      }
      itemData
    }

    def ItemInfoFromIndex(index:Int):ItemInfo = {
      val value = this.itemCollection.getDataList(index)
      ItemInfo(value,this.itemGenerator.ContainerFromIndex(index),index)
    }
}


object ItemsControl {
  def GetItemsOwner(panel:Panel):Option[ItemsControl] = {
    panel.templateParent match {
        case Some(value:ItemsPresenter) if panel.isItemsHost => value.getItemsControl
        case _ => None
      }
  }
}

 case class ItemInfo(item: Any, var container: Option[UIElement],var index:Int = -1) {
    def Update(generator: ItemContainerGenerator): Unit = {
      if (this.index < 0 && this.container.isDefined) {
        this.index = generator.IndexFromItemData(this.container.get)
      }
    }

    override def equals(x: Any): Boolean = {
      x match
        case other: ItemInfo => {
          if (other.container.isDefined && this.container.isDefined) {
            return other.container == this.container;
          } else {
            other.item == this.item
          }
        }
        case _ => false
    }

  }
