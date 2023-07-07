package ui.controls
import core.reflect.*;
import scala.collection.mutable.ArrayBuffer
import scala.collection.IndexedSeq;
import ui.ContentProperty;
import core.logError;
import scala.util.Try
import scala.util.Failure
@ContentProperty("items")
class ItemsControl extends Control with IDataElementGenerator derives ReflectType {
    var items:ArrayBuffer[Any] = ArrayBuffer.empty;
    var itemsSource:IndexedSeq[Any] = null;
    var itemTemplate:Option[DataTemplate] = None;
    var warpElement:UIElement = StackPanel();
    
    var itemCollection:ItemCollection = ItemCollection(this)

    private var realWarpPanel:UIElement = null;
    override def Awake(): Unit = {
      super.Awake();
    }

    override def OnEnter(): Unit = {
      if(this.items.length > 0) {
        this.itemCollection.setItemSource(items);
      }
      if(this.itemsSource != null) {
        this.itemCollection.setItemSource(this.itemsSource);
      }
      this.realWarpPanel = warpElement.clone();
      super.OnEnter();
    }

    def getWarpPanel():UIElement = this.realWarpPanel

    def genElement(data:Any):Try[UIElement] = {
      if(itemTemplate.isDefined) {
        val tryElement = this.itemTemplate.get.LoadContent(this,None).logError();
        tryElement.foreach(item => item.dataContext = data);
        return tryElement;
      }
      Failure(Exception("not found itemTemplate"))
    }
}


