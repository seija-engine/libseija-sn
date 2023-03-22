import scalanative.unsigned.UnsignedRichInt
import scala.scalanative.unsafe
import scala.scalanative.runtime.libc
import core.CoreModule
import window.WindowModule
import transform.TransformModule
import asset.AssetModule
import input.InputModule
import input.FFISeijaInput
import core.FFISeijaCore
import scala.scalanative.unsafe._




object Main {
  def OnStart() :Unit = {
      println("OnStart");
  }
  
  def main(args: Array[String]): Unit = {
    val app = core.App();
    app.addModule(CoreModule());
    app.addModule(AssetModule("./res/"));
    app.addModule(TransformModule());
    app.addModule(WindowModule());
    app.addModule(InputModule());
    
    val startPtr = CFuncPtr.toPtr(CFuncPtr0.fromScalaFunction(() => {
      println("closure?");
    }));  
    FFISeijaCore.appSetOnStart(app.ptr,startPtr);

    /*
    app.addModule(render.RenderModule(render.RenderConfig(
      "./res/config.json",
      "./res/script.lua",
      List("./res/render_libs/")
    )));*/
    app.start();
    app.run();
  }

  
}




