package ui.controls
import core.reflect.*;
import scala.collection.mutable.ArrayBuffer
import scala.collection.Seq;
import ui.ContentProperty;

@ContentProperty("items")
class ItemsControl extends Control derives ReflectType {
    var items:ArrayBuffer[Any] = ArrayBuffer.empty;
    var itemsSource:Seq[Any] = null;
    var itemTemplate:Option[DataTemplate] = None;

  
    override def OnEnter(): Unit = {
      super.OnEnter();
    }
}