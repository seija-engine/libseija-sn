package ui.controls
import core.reflect.ReflectType;
import ui.xml.IControlFromXml
import ui.xml.setXmlStringPropery
import core.formString;
import math.Color
import ui.Template

class Button extends BaseLayout derives ReflectType {
    protected var _template:Option[Template] = None;
    def template = this._template
    def template_=(value: Template): Unit = {  this._template = Some(value); }
}


object Button {
    given IControlFromXml[Button] with {
        val name:String = "Button"
        def create():Button = new Button()

        override def setStringPropery(control:Button,name:String,value:String):Unit = {
            setXmlStringPropery[BaseLayout](control,name,value);
            name match
                case _ => {} 
        }
    }
}