package ui.controls
import ui.ContentProperty
import core.reflect.*

@ContentProperty("content")
class ContentControl extends Control derives ReflectType {
    var content:Any = _
    var contentTemplate:Option[DataTemplate] = None
}