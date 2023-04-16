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
import ui.SpriteSheetAsset;
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
import ui.{
  UISystem,
  UISystemComponent,
  Rect2D,
  UICanvas,
  UICanvasComponent,
  Rect2dComponent,
  getIndex,
  FFISeijaUI,
  SpriteSheet,
  CanvasComponent,
  UIModule,
  Canvas,
  Sprite,
  SpriteComponent
}

object Main {
  def main(args: Array[String]): Unit = {
    val file = java.io.File("");
    val app = core.App;
    FFISeijaCore.initLog("ERROR");
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
    app.addModule(UIModule());

    app.start(new DemoGame());
    app.run();
  }
}

class DemoGame extends IGameApp {
  var testTexure: HandleUntyped = null;
  def OnStart() = {
    println("DemoGame.OnStart");
    this.init2D();
  }

  def init2D() = {
    val hSheet: Handle[SpriteSheet] =
      Assets.loadSync[SpriteSheet]("ui/ui.json").get;
    val sheet = FFISeijaUI.spriteSheetAssetGet(core.App.worldPtr, hSheet.id.id)
    val spriteIndex = sheet.getIndex("Btn3On").get

    val ui_camera = Entity
      .spawnEmpty()
      .add[Transform]()
      .add[Camera](cam => cam.sortType = 1)
      .add[UICanvas]()
      .add[UISystem]()

    Entity.spawnEmpty()
          .add[Transform](t => {
            t.parent = Some(ui_camera);
            t.position = Vector3(0,0,-2);
          })
          .add[Rect2D](r => {
            r.width = 100;
            r.height = 45;
          })
          .add[Canvas]()
          .add[Sprite](s => {
            s.atlas = hSheet;
            s.spriteIndex = spriteIndex;
          })
  }

  def init3D() = {
    val cameraEntity = Entity
      .spawnEmpty()
      .add[Transform](t => t.position = Vector3(0, 0, 2))
      .add[Camera](v => {
        v.projection = render.Projection.Per(render.Perspective())
      });
    val hTexure = Assets.loadSync[Texture]("texture/b.jpg").get;
    val hMesh = Assets.get[Mesh]("mesh:cube", true).get
    val hMaterial = Assets.loadSync[Material]("mats/color.mat.json").get;
    val cubeEntity = Entity
      .spawnEmpty()
      .add[Transform](v => v.position = Vector3(0, 0, -1))
      .add[Handle[Mesh]](_.mesh = hMesh)
      .add[Handle[Material]](_.material = hMaterial);

  }

  def OnUpdate() = {
    if (Input.getKeyDown(KeyCode.A)) {} else if (Input.getKeyUp(KeyCode.A)) {}

  }
}
