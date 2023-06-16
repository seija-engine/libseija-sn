import core.FFISeijaCore
import core.CoreModule
import asset.AssetModule
import transform.TransformModule
import window.WindowModule
import input.InputModule
import ui.UIModule
import java.net.SocketAddress
import java.nio.channels.AsynchronousSocketChannel
import java.net.InetAddress
import java.net.InetSocketAddress
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Buffer
import scala.collection.mutable.HashMap
import ui.xml.XmlNSResolver
import core.reflect.Assembly
import ruv.RUVModule;
object Main {
  def main(args: Array[String]): Unit = {
    runSeija();
  }

  def runSeija() = {
    val app = core.App;
    FFISeijaCore.initLog("ERROR");
    app.addModule(CoreModule());
    app.addModule(AssetModule("example/assets"));
    app.addModule(TransformModule());
    app.addModule(WindowModule());
    app.addModule(RUVModule());
    app.addModule(InputModule());
    app.addModule(
      render.RenderModule(
        render.RenderConfig(
          "example/.shader",
          "example/script/render.clj",
          List("example/script")
        )
      )
    );
    
    
    app.addModule(UIModule());
    app.start(new TestDemo());
    app.run();
   
  }
}
