package ui.controls
import core.reflect.*;
import scala.collection.mutable.ArrayBuffer
import scala.collection.IndexedSeq;
import ui.ContentProperty;
import core.logError;
import scala.util.Try
import scala.util.Failure
import scala.util.Success
@ContentProperty("items")
class ItemsControl extends Control with IDataElementGenerator derives ReflectType {
    var items:ArrayBuffer[Any] = ArrayBuffer.empty;
    var itemsSource:IndexedSeq[Any] = null;
    var itemTemplate:Option[DataTemplate] = None;
    var warpElement:UIElement = this.defaultWrapPanel
    
    var itemCollection:ItemCollection = ItemCollection(this)

    private var realWarpPanel:UIElement = null;
    override def Awake(): Unit = {
      super.Awake();
    }

    override def OnEnter(): Unit = {
      if(this.items.nonEmpty) {
        this.itemCollection.setItemSource(items);
      }
      if(this.itemsSource != null) {
        this.itemCollection.setItemSource(this.itemsSource);
      }
      this.realWarpPanel = warpElement.clone();
      super.OnEnter();
    }

    protected def defaultWrapPanel:Panel = StackPanel()

    def getWarpPanel:UIElement = this.realWarpPanel

    def genElement(data:Any):Try[UIElement] = {
      this.itemTemplate.orElse(this.findDataTemplate(data.getClass().getName())) match {
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
}


