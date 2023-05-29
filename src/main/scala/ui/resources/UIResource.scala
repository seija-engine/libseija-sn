package ui.resources
import core.reflect.*;
import ui.ContentProperty;
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import scala.concurrent.Await

@ContentProperty("resList")
class UIResource derives ReflectType {
    var resList:ArrayBuffer[BaseUIResource] = ArrayBuffer[BaseUIResource]()
}

object UIResource {
    def empty():UIResource = { new UIResource(); }
}

class BaseUIResource {
   var key:String = "";
}
