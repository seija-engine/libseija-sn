package ui.resources
import core.reflect.*;
import ui.ContentProperty;

@ContentProperty("value")
class Setter derives ReflectType {
    var key:String = "";
    var value:Any = null;
}