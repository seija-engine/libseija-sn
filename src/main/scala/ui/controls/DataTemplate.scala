package ui.controls
import ui.controls.BaseTemplate
import core.reflect.*;
import ui.ContentProperty

@ContentProperty("content")
class DataTemplate extends BaseTemplate derives ReflectType {
    var content:UIElement = UIElement.zero;
}