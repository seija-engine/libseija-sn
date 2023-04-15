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
import transform.{Transform, TransformComponent}
import render.{
  Camera,
  CameraComponent,
  MaterialComponent,
  TextureAssetType,
  Mesh,
  MeshComponent,
  Material,
  MaterialAssetType
}
import scalanative.unsafe.CFuncPtr1.fromScalaFunction
import transform.setPosition
import asset.FFISeijaAsset
import asset.Assets
import asset.{HandleUntyped, Handle}
import render.FFISeijaRender
import render.Texture

object Main {
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

    app.addModule(
      render.RenderModule(
        render.RenderConfig(
          "example/.shader",
          "example/script/render.clj",
          List("example/script")
        )
      )
    );
    app.start(new DemoGame());
    app.run();
  }
}

class DemoGame extends IGameApp {
  var testTexure: HandleUntyped = null;
  def OnStart() = {
    println("OnStart");
    val cameraEntity = Entity.spawnEmpty().add[Transform](t => t.position = Vector3(0,0,2)).add[Camera](v => {
      v.projection = render.Projection.Per(render.Perspective())
    });
    val hTexure = Assets.loadSync[Texture]("texture/b.jpg").get;
    val hMesh = Assets.get("mesh:cube", true).get.typed[Mesh]()
    val hMaterial = Assets.loadSync[Material]("mats/color.mat.json").get.typed[Material]();
    val cubeEntity = Entity.spawnEmpty()
                           .add[Transform](v => v.position = Vector3(0, 0, -1))
                           .add[Handle[Mesh]](_.mesh = hMesh)
                           .add[Handle[Material]](_.material = hMaterial);

  }

  def OnUpdate() = {
    if (Input.getKeyDown(KeyCode.A)) {} else if (Input.getKeyUp(KeyCode.A)) {}

  }
}
