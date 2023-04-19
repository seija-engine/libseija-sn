import ui.core.FFISeijaUI
import ui.core.Rect2D
import ui.core.{Sprite, SpriteType}
import ui.core.{SpriteSheet, getIndex}
import ui.core.UICanvas
import ui.core.UIModule
import ui.core.UISystem
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
import scalanative.unsafe.CFuncPtr3.fromScalaFunction
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
import java.util.ArrayList;
import scalanative.unsafe.CFuncPtr1.fromScalaFunction
import transform.setPosition
import asset.FFISeijaAsset
import asset.Assets
import asset.{HandleUntyped, Handle}
import render.FFISeijaRender
import render.Texture

import ui.core.Canvas

import ui.core.{
  FlexLayoutComponent,
  FlexItemComponent,
  Orientation,
  ItemLayoutComponent,
  EventNode,
  EventNodeComponent,
  StackLayoutComponent,
  SizeValue,Font,FontAssetType,
  LayoutAlignment,
  Text,TextComponent
}
import ui.core.given;
import scala.scalanative.unsafe.Tag.UInt
import scala.scalanative.unsigned.UInt
import ui.core.StackLayout
import ui.core.StackLayout
import ui.core.Thickness
import ui.core.ItemLayout
import ui.core.FlexLayout
import ui.core.FlexItem
import ui.core.Text
import ui.core.Font

object Main {
  def main(args: Array[String]): Unit = {
    val file = java.io.File("");
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
    app.addModule(UIModule());

    app.start(new DemoGame());
    app.run();
  }
}

object DemoGame {
  var events: ArrayList[Long] = ArrayList();
}

class DemoGame extends IGameApp {
  var testTexure: HandleUntyped = null;
  var entityStack: Entity = Entity(0);
  var hSheet: Handle[SpriteSheet] = null;
  var bgSpriteIndex: Int = 0;
  var btnSpriteIndex: Int = 0;
  var font: Handle[Font] = null;
  def OnStart() = {
    println("DemoGame.OnStart");
    this.init2D();
  }

  def init2D() = {
    this.hSheet = Assets.loadSync[SpriteSheet]("ui/ui.json").get;
    val sheet = FFISeijaUI.spriteSheetAssetGet(core.App.worldPtr, hSheet.id.id)
    this.bgSpriteIndex = sheet.getIndex("lm-db").get
    this.btnSpriteIndex = sheet.getIndex("Btn3On").get
    val ui_camera = Entity
      .spawnEmpty()
      .add[Transform]()
      .add[Camera](cam => cam.sortType = 1)
      .add[UICanvas]()
      .add[UISystem]()
    this.font = Assets.loadSync[Font]("ui/WenQuanYiMicroHei.ttf").get
    println(this.font)
    this.createFlex(ui_camera);
  }

  def createFlex(uiCamera: Entity): Unit = {
    val flexEntity = Entity
      .spawnEmpty()
      .add[Transform](t => {
        t.parent = Some(uiCamera); t.position = Vector3(0, 0, -2);
      })
      .add[Rect2D]()
      .add[Canvas]()
      .add[Sprite](s => {
        s.atlas = hSheet;
        s.spriteIndex = bgSpriteIndex;
        s.typ = SpriteType.Slice(Thickness(30))
      })
      .add[FlexLayout](v => {
        v.common.padding = Thickness(30)
        v.alignItems = ui.core.FlexAlignItems.Start;
        v.justify = ui.core.FlexJustify.Start;
        v.direction = ui.core.FlexDirection.Column;
      });
    val fstEntity = Entity
      .spawnEmpty()
      .add[Transform](t => { t.parent = Some(flexEntity); })
      .add[Rect2D]()
      .add[ItemLayout](v => {
        v.common.uiSize.width = SizeValue.Pixel(150);
        v.common.uiSize.height = SizeValue.Pixel(50);
        //v.common.hor = LayoutAlignment.Stretch;
        //v.common.ver = LayoutAlignment.Start;
      })
      .add[FlexItem]()
      .add[Sprite](v => {
        v.typ = SpriteType.Slice(Thickness(30)); v.atlas = hSheet;
        v.spriteIndex = this.btnSpriteIndex;
      });
    Entity
      .spawnEmpty()
      .add[Transform](t => { t.parent = Some(flexEntity); })
      .add[Rect2D]()
      .add[ItemLayout](v => {
        v.common.uiSize.width = SizeValue.Auto;
        v.common.uiSize.height = SizeValue.Pixel(50);
        v.common.hor = LayoutAlignment.Stretch;
        //v.common.ver = LayoutAlignment.Start;
      })
      .add[FlexItem](item => item.grow = 1)
      .add[Sprite](v => {
        v.typ = SpriteType.Slice(Thickness(30)); v.atlas = hSheet;
        v.spriteIndex = this.btnSpriteIndex;
      });
    
    Entity.spawnEmpty().add[Transform](t => t.parent = Some(fstEntity)).add[Rect2D]().add[Text](text => {
      text.text = "测试文本";
      text.font = this.font;
      text.color = Vector4(0,0.1,1,1);
    })
  }

  def createTestStack(ui_camera: Entity) = {
    this.entityStack = Entity
      .spawnEmpty()
      .add[Transform](t => {
        t.parent = Some(ui_camera);
        t.position = Vector3(0, 0, -2);
      })
      .add[Rect2D]()
      .add[Canvas]()
      .add[Sprite](s => {
        s.atlas = hSheet;
        s.spriteIndex = bgSpriteIndex;
        s.typ = SpriteType.Slice(Thickness(30))
      })
      .add[EventNode](v => {
        v.eventType = EventNode.TOUCH_START | EventNode.TOUCH_END;
        v.userKey = "传递String";
      })
      .add[StackLayout](v => {
        v.common.hor = LayoutAlignment.Stretch;
        v.common.ver = LayoutAlignment.Stretch;
        v.common.padding = Thickness(20)
        v.spacing = 10;
        v.orientation = Orientation.Vertical;
      })
    for (i <- 1 to 7) {
      Entity
        .spawnEmpty()
        .add[Transform](t => { t.parent = Some(this.entityStack); })
        .add[Rect2D]()
        .add[ItemLayout](v => {
          v.common.uiSize.width = SizeValue.Pixel(120);
          v.common.uiSize.height = SizeValue.Pixel(50);
        })
        .add[Sprite](v => {
          v.typ = SpriteType.Slice(Thickness(30)); v.atlas = hSheet;
          v.spriteIndex = this.btnSpriteIndex;
        })
    }
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
    FFISeijaUI.readUIEvents(
      core.App.worldPtr,
      (entityId: Long, typ: UInt, keyPtr: Ptr[Byte]) => {
        val keyString = fromCString(keyPtr);
        DemoGame.events.add(entityId);
      }
    );

    if (DemoGame.events.size() > 0) {
      val rawStack = this.entityStack.get[StackLayout]()
      // rawStack.setPadding(Thickness(30))
      // rawStack.setOrientation(Orientation.Horizontal)
      rawStack.getOrientation() match
        case Orientation.Horizontal =>
          rawStack.setOrientation(Orientation.Vertical)
        case Orientation.Vertical =>
          rawStack.setOrientation(Orientation.Horizontal)

    }
    DemoGame.events.clear();
  }
}
