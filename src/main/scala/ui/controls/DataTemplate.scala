package ui.controls
import ui.controls.BaseTemplate
import core.reflect.*;
import ui.ContentProperty
import ui.resources.BaseUIResource;
import scala.util.Try
import scala.util.Success
@ContentProperty("content")
class DataTemplate extends BaseTemplate with BaseUIResource derives ReflectType {
    var key:String = "";
    var dataType:String = "";
    def getKey: String = this.key;
    
    var content:UIElement = UIElement.zero;

    override def LoadContent(parent: UIElement): Try[UIElement] = {
        val instObject:UIElement = content.clone();
        this.setUIElementTemplate(instObject,parent);
        Success(instObject)
    }
}