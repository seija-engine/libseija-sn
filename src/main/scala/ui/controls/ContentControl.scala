package ui.controls
import ui.controls.template.DataTemplate
import ui.ContentProperty;
import core.reflect.*;
import scala.runtime.Static


 
@ContentProperty("content")
class ContentControl extends Control derives ReflectType {
    var content:Option[Any] = None
    var dataTemplate:Option[DataTemplate] = None
}