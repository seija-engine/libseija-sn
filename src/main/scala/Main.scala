import ui.controls.Image
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
  SizeValue,
  Font,
  FontAssetType,
  LayoutAlignment,
  Text,
  TextComponent
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
import ui.Atlas
import ui.PropertyChangedEventArgs
import ui.usl.UDSL;
import scala.collection.mutable.HashMap
import ui.usl.TestOpCodes
import ui.usl.types.given;
import core.xml.FFIXml
import scala.util.control.Breaks._
import core.xml.{XmlReader,XmlEvent}
import ui.xml.XmlControl
import core.StringObject
object Main {
  def main(args: Array[String]): Unit = {
    println("runMain")
    
    StringObject.register("Image",ui.controls.ImageFormString)
    
    XmlControl.fromString("<Image width=150 height=50> </Image>")
  }

  def runSeija() = {
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

object DemoGame {
  var events: ArrayList[Long] = ArrayList();
}

class DemoGame extends IGameApp {
  var testTexure: HandleUntyped = null;
  var entityStack: Entity = Entity(0);
  var hSheet: Handle[SpriteSheet] = null;
  var bgSpriteIndex: Int = 0;
  var bgSprite2Index: Int = 0;
  var btnSpriteIndex: Int = 0;
  var font: Handle[Font] = null;
  def OnStart() = {
    val canvas = ui.UICanvas.create();

  }

  def OnUpdate() = {}
}

/*
控件树：
  Panel
    Button

View树:
  ImageBG                 -Panel
    BorderImage           -Panel
  ChildAttach[padding 20] -Panel的子控件槽
    ButtonBG              --Button
      ButtonText          --Button
    
  
*/