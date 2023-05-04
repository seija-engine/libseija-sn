import ui.xml.XmlControl
import core.FFISeijaCore
import core.{CoreModule,Entity}
import asset.AssetModule
import transform.TransformModule
import window.WindowModule
import input.InputModule
import ui.UIModule
import java.util.ArrayList
import core.IGameApp
import asset.HandleUntyped
import asset.Handle
import ui.core.SpriteSheet
import ui.core.Font
import ui.controls.{CheckBox,Image}
import ui.controls.given
import scala.util.Failure
import scala.util.Success
import ui.UICanvas
import Main.testXml
import ui.Atlas
import ui.core.Thickness
import scala.deriving.Mirror
import ui.BaseControl



case class TypeInfo(val name:String,val fieldList:List[FieldInfo]);

case class FieldInfo(val Name:String,set:(Any,Any) => Unit,get:(Any) => Any);


trait TypeObject[T] {
    extension(v:T) def info:TypeInfo;
}

given TypeObject[A] with {
  extension (v: A) override def info: TypeInfo = {
    TypeInfo("A",List(
        FieldInfo("fa",(obj,field) => {
            obj.asInstanceOf[A].fa = field.asInstanceOf[Int]
        },_.asInstanceOf[A].fa)
    ))
  }
}

class A {
    var fa:Int = 0;
    def setValue(name:String,value:Any) = {
        name match
            case "fa" => this.fa = value.asInstanceOf[Int];
            case _: String => 
    }
    def getValue(name:String):Any = {
        name match
            case "fa" => this.fa
    }
}



object Main {
  val testXml = """
      <CheckBox hor='Center' ver='Center' width='25' height='25' >
        <CheckBox.Template>
          <Image sprite="default.duikong"  />
          <Image sprite="{Binding Owner checked}"  />
        </CheckBox.Template>
      </CheckBox>
  """
  def main(args: Array[String]): Unit = {
    XmlControl.register[CheckBox]();
    XmlControl.register[Image]();
   
    val sss = ui.Atlas.testMacro[ui.controls](123);
    println(s"testMacro:${sss}")
    //runSeija(); 
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
    Atlas.load("default","ui/default.json").get
    val bgSprite = Atlas.getPath("default.dk2").get;
    bgSprite.sliceInfo = Some(Thickness(30))

    val image = Image();
    image.sprite = Some(bgSprite)
    image.imageType = ui.controls.ImageType.Slice;
    canvas.addControl(image);


    val uiControl = XmlControl.fromString(testXml).get;
    canvas.addControl(uiControl);
  }

  def OnUpdate() = {}
}