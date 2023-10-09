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
import ui.core.SizeValue
import ui.resources.Style

@ContentProperty("items")
class ItemsControl extends Control with IDataElementGenerator with IGeneratorHost derives ReflectType {
    var items:ArrayBuffer[Any] = ArrayBuffer.empty
    protected var _itemsSource:IndexedSeq[Any] = null
    var itemTemplate:Option[DataTemplate] = None;
    //TODO delete it
    var warpElement:UIElement = this.defaultWrapPanel

    var _ItemContainerStyle:Option[Style] = None;
    def ItemContainerStyle:Option[Style] = this._ItemContainerStyle
    def ItemContainerStyle_=(value:Option[Style]):Unit = {
      this._ItemContainerStyle = value;callPropertyChanged("ItemContainerStyle",this)
    }
    
    var itemCollection:ItemCollection = ItemCollection(this)
    var itemGenerator:ItemContainerGenerator = ItemContainerGenerator(this)

    override def View: ItemCollection = this.itemCollection

    var itemsPanel:Option[ItemsPanelTemplate] = None

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
      this.updateHasItems()
      if(this.items.nonEmpty) {
        this.itemCollection.setItemSource(items)
      }
      if(this.itemsSource != null) {
        this.itemCollection.setItemSource(this.itemsSource)
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
          tryElement.foreach(_.setLogicParent(Some(this)))
          tryElement
        }
        case _ => {
          if(data.isInstanceOf[UIElement]) {
            val newElement = data.asInstanceOf[UIElement]
            newElement.setLogicParent(Some(this))
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
        case _ =>
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