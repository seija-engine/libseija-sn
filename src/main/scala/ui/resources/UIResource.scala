package ui.resources
import core.reflect.*;
import ui.ContentProperty;
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import scala.concurrent.Await
import scala.collection.mutable.{Buffer,Growable,HashMap}
import scala.collection.mutable.Growable
import core.reflect.*;

@ContentProperty("resList")
class UIResource extends Growable[BaseUIResource] derives ReflectType {

    var resList:ArrayBuffer[BaseUIResource] = ArrayBuffer[BaseUIResource]()

    protected var styleDict:HashMap[String,Style] = HashMap.empty 

    def findStyle(key:String):Option[Style] = {
        None
    }

    override def clear(): Unit = this.resList.clear();

    override def addOne(elem: BaseUIResource): this.type = {
        this.resList.addOne(elem)
        elem match {
            case style:Style => {
                println(s"adding style ${style.key} ${style.forType}")
                this.styleDict.put(style.key,style)
            }
        }
        this
    }
}

object UIResource {
    def empty():UIResource = { new UIResource(); }
}

class BaseUIResource derives ReflectType {
   var key:String = "";
}
