package ui.controls
import ui.controls.BaseTemplate
import core.reflect.*;
import ui.ContentProperty
import scala.collection.mutable.ArrayBuffer
import scala.util.Try
import core.ICopy;
import core.copyObject
import scala.util.Success

@ContentProperty("content")
class ControlTemplate extends BaseTemplate derives ReflectType {
    var content:UIElement = UIElement.zero;

    override def LoadContent(parent:UIElement): Try[UIElement] = {
        val instObject:UIElement = content.clone();
        setUIElementTemplate(instObject,parent);
        Success(instObject)
    }

    
}