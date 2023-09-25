package ui.controls
import core.reflect.*

import scala.collection.mutable.ArrayBuffer
import scala.collection.IndexedSeq
import ui.ContentProperty
import core.logError
import ui.binding.{INotifyCollectionChanged, NotifyCollectionChangedEventArgs}

import scala.util.Try
import scala.util.Failure
import scala.util.Success
@ContentProperty("items")
class ItemsControl extends Control with IDataElementGenerator derives ReflectType {
    var items:ArrayBuffer[Any] = ArrayBuffer.empty
    protected var _itemsSource:IndexedSeq[Any] = null
    var itemTemplate:Option[DataTemplate] = None;
    var warpElement:UIElement = this.defaultWrapPanel
    
    var itemCollection:ItemCollection = ItemCollection(this)
    private var realWarpPanel:UIElement = null
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
      if(this.items.nonEmpty) {
        this.itemCollection.setItemSource(items);
      }
      if(this.itemsSource != null) {
        this.itemCollection.setItemSource(this.itemsSource);
      }
      this.realWarpPanel = warpElement.clone();
      super.OnEnter()
    }

    protected def defaultWrapPanel:Panel = StackPanel()

    def getWarpPanel:UIElement = this.realWarpPanel

    def genElement(data:Any):Try[UIElement] = {
      this.itemTemplate.orElse(this.findDataTemplate(data.getClass.getName)) match {
        case Some(template) => {
          val tryElement = template.LoadContent(this,None).logError()
          tryElement.foreach(item => item.dataContext = data)
          tryElement
        }

        case _ => {
          if(data.isInstanceOf[UIElement]) {
            val newElement = data.asInstanceOf[UIElement]
            return Success(newElement.clone())
          }
          Failure(Exception("not found itemTemplate"))
        }
      }
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
}


