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
import transform.{Transform,TransformComponent}
import transform.FFISeijaTransform
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
    println("OnStart");
    val entity = Entity.spawnEmpty().add[Transform](v => {
      v.position = new Vector3(1,2,3);
      v.quat = new Quat(4,5,6,7);
      v.scale = new Vector3(8,9,10);
    });
    FFISeijaTransform.transformDebugLog(core.App.worldPtr,entity);
  }
  
  def OnUpdate() = {
    
    if(Input.getKeyUp(KeyCode.A)) {
      println("A");
    }
   
  }
}




