package ui.controls
import ui.controls.BaseTemplate
import core.reflect.*;
import ui.ContentProperty
import ui.resources.BaseUIResource;
@ContentProperty("content")
class DataTemplate extends BaseTemplate with BaseUIResource derives ReflectType {
    var key:String = "";
    var dataType:String = "";
    def getKey: String = this.key;
    
    var content:UIElement = UIElement.zero;
}