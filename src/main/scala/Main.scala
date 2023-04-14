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
import render.{Camera,CameraComponent}
import scalanative.unsafe.CFuncPtr1.fromScalaFunction
import transform.setPosition

object Main {

  def OnStart() :Unit = {
      println("OnStart");
  }
  
  def main(args: Array[String]): Unit = {
    val file = java.io.File("");
    println(file.getAbsolutePath());
    val app = core.App;
    FFISeijaCore.initLog("INFO");
    app.addModule(CoreModule());
    app.addModule(AssetModule("./res/"));
    app.addModule(TransformModule());
    app.addModule(WindowModule());
    app.addModule(InputModule());
   
    app.addModule(render.RenderModule(render.RenderConfig(
      "example/.shader",
      "example/script/render.clj",
      List("example/script")
    )));
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
    }).add[Camera](v => {
      
    });
    /*
    
    FFISeijaTransform.transformDebugLog(core.App.worldPtr,entity);
    val rawT = entity.get[Transform]()
    val entity2 = Entity.spawnEmpty().add[Transform]();
    rawT.setPosition(Vector3(6,66,666))
    FFISeijaTransform.transformDebugLog(core.App.worldPtr,entity);
    */
  }

  
  def OnUpdate() = {
    
    if(Input.getKeyUp(KeyCode.A)) {
      println("A");
    }
   
  }
}




