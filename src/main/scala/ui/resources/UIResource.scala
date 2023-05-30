package ui.resources
import core.reflect.*;
import ui.ContentProperty;
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import scala.concurrent.Await
import scala.collection.mutable.Buffer
import scala.collection.mutable.Growable
import core.reflect.*;

@ContentProperty("resList")
class UIResource extends Growable[BaseUIResource] derives ReflectType {

    override def clear(): Unit = this.resList.clear();

    override def addOne(elem: BaseUIResource): this.type = {
        this.resList.addOne(elem)
        this
    }

    var resList:ArrayBuffer[BaseUIResource] = ArrayBuffer[BaseUIResource]()
}

object UIResource {
    def empty():UIResource = { new UIResource(); }
}

class BaseUIResource derives ReflectType {
   var key:String = "";
}
