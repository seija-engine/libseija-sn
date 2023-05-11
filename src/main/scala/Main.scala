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
import ui.controls.{CheckBox,Image,BaseLayout,ImageType}
import ui.controls.given
import scala.util.Failure
import scala.util.Success
import ui.UICanvas
import math.{Color,given};
import ui.{Atlas,given}
import ui.binding.given;
import ui.binding.BoolAtlasSprite
import ui.core.Thickness
import scala.deriving.Mirror
import ui.BaseControl
import core.reflect.Assembly
import _root_.core.reflect.*
import math.Vector4
object Main {
  val testXml = """
      <CheckBox hor='Center' checked="true" ver='Center' width='16' height='16' >
        <CheckBox.Template>
          <Image sprite="{Binding Owner checked Conv=ui.BoolAtlasSprite(default.checkbox-checked,default.checkbox-unchecked) Type=Src2Dst}"  />
        </CheckBox.Template>
      </CheckBox>
  """
  def main(args: Array[String]): Unit = {
    XmlControl.register[CheckBox]();
    XmlControl.register[Image]();

    Assembly.add[BaseLayout]();
    Assembly.add[CheckBox]()
    Assembly.add[Image]()
    Assembly.add[BoolAtlasSprite]();
    Assembly.add[TestViewModel]()
    runSeija(); 
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
    val bgSprite = Atlas.getPath("default.white").get;
    val sprite2 = Atlas.getPath("default.button").get;
    sprite2.sliceInfo = Some(Thickness(5,5,5,5));

    val image = Image();
    image.sprite = Some(bgSprite)
    image.color = Color.formHex("#e8e8e7").get;    
    canvas.addControl(image);

    /*
    val image2 = Image();
    image2.imageType = ImageType.Slice;
    image2.sprite = Some(sprite2)
    image2.width = ui.core.SizeValue.Pixel(100);
    image2.height = ui.core.SizeValue.Pixel(30);
    image2.color = Color.formHex("#2e3436").get;
    println(image2.color)
    canvas.addControl(image2);*/


    var testViewModel = new TestViewModel();
    val uiControl = XmlControl.fromString(Main.testXml).get;
    uiControl.dataContext = testViewModel;
    canvas.addControl(uiControl);
  }

  def OnUpdate() = {}
}

class TestViewModel {
  var isTest:Boolean = true;
}

given ReflectType[TestViewModel] with {
  override def info: TypeInfo = TypeInfo("TestViewModel",
  () => new TestViewModel(),None,List(
    FieldInfo("isTest",
     (a,b) => a.asInstanceOf[TestViewModel].isTest = b.asInstanceOf[Boolean],
     (a) => a.asInstanceOf[TestViewModel].isTest
    )
  ))
}