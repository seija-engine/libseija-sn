package ui.controls2
import ui.controls2.template.DataTemplate
import ui.ContentProperty;
import core.reflect.*;
import scala.runtime.Static


 
@ContentProperty("content")
class ContentControl extends Control derives ReflectType {
    var content:Option[Any] = None
    var dataTemplate:Option[DataTemplate] = None
}