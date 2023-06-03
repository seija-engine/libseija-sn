package ui.resources
import core.reflect.*;
import ui.ContentProperty;
import scala.collection.mutable.ArrayBuffer

@ContentProperty("setterList")
class Style extends BaseUIResource derives ReflectType {
    var forType:String = "";
    var setterList:ArrayBuffer[Setter] = ArrayBuffer[Setter]()
}