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
import core.IGameApp
import input.Input
import input.KeyCode
import core.Entity
import math._;
import core.{*,given}
object Main {

  def OnStart() :Unit = {
      println("OnStart");
  }
  
  def main(args: Array[String]): Unit = {
    val app = core.App;
    app.addModule(CoreModule());
    app.addModule(AssetModule("./res/"));
    app.addModule(TransformModule());
    app.addModule(WindowModule());
    app.addModule(InputModule());

    /*
    app.addModule(render.RenderModule(render.RenderConfig(
      "./res/config.json",
      "./res/script.lua",
      List("./res/render_libs/")
    )));*/
    app.start(new DemoGame());
    app.run();
  } 
}



class DemoGame extends IGameApp {
  def OnStart() = {
     Entity.spawn().add[TestData]((cache) => {
        cache.pos = Vector3.zero;
     });
  }
  
  def OnUpdate() = {
    
    if(Input.getKeyUp(KeyCode.A)) {
      println("A");
    }
   
  }
}




