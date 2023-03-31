package window
import core._;
import scala.scalanative.unsafe._


case class WindowModule(
    val width:Float = 1024f,
    val height:Float = 768f,
    val title:String = "Seija In Scala Native"
) extends IModule {
    def OnAdd(appPtr:Ptr[Byte]):Unit = {
       val configPtr = FFISeijaWindow.newWindowConfig();
       configPtr._1 = width;
       configPtr._2 = height;
       
       FFISeijaWindow.SetConfigTitle(configPtr,title);
       FFISeijaWindow.addWinitModule(appPtr,configPtr);
    }
}
