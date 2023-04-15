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
import render.{Camera,CameraComponent,TextureType}
import scalanative.unsafe.CFuncPtr1.fromScalaFunction
import transform.setPosition
import asset.FFISeijaAsset
import asset.Assets
import asset.HandleUntyped

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
    app.addModule(AssetModule("example/assets"));
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
  var testTexure:HandleUntyped = null;
  def OnStart() = {
    println("OnStart");
    val cameraEntity = Entity.spawnEmpty().add[Transform]().add[Camera]();
    val cubeEntity = Entity.spawnEmpty().add[Transform]();
  }

  
  def OnUpdate() = {
    
    if(Input.getKeyDown(KeyCode.A)) {
      this.testTexure = Assets.loadSync("texture/b.jpg").get;
      println(this.testTexure);
    } else if(Input.getKeyUp(KeyCode.A)) {
      //Assets.unload(this.testTexure);
    }
   
  }
}




