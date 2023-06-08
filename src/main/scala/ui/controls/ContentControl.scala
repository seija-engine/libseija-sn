package ui.controls
import ui.controls.DataTemplate
import ui.ContentProperty;
import core.reflect.*;
import scala.runtime.Static


 
@ContentProperty("content")
class ContentControl extends Control derives ReflectType {
    var content:Any = null;
    var dataTemplate:Option[DataTemplate] = None
}