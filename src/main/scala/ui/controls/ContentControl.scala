package ui.controls
import ui.controls.DataTemplate
import ui.ContentProperty;
import core.reflect.*;
import scala.runtime.Static


 
@ContentProperty("content")
class ContentControl extends Control derives ReflectType {
    var content:Option[Any] = None
    var dataTemplate:Option[DataTemplate] = None

    override def OnEnter(): Unit = {
       super.OnEnter();
      
    }
}