package ui.controls
import ui.controls.BaseTemplate
import core.reflect.*;
import ui.ContentProperty
import scala.collection.mutable.ArrayBuffer

@ContentProperty("content")
class ControlTemplate extends BaseTemplate derives ReflectType {
    var content:UIElement = UIElement.zero;
}