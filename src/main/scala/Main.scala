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
import ui.controls.{CheckBox,Button,Image,BaseLayout,ImageType,StackLayout,Text}
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
import scala.util.boundary

object Main {
  val testXml = """
      <StackLayout orientation="Ver" spacing="10" padding="20,0,0,0">
        <CheckBox checked="true" hor="Center" ver="Center" width='16' height='16' >
          <CheckBox.Template>
            <Image sprite="{Binding Owner checked Conv=BoolAtlasSprite(default.checkbox-checked,default.checkbox-unchecked) Type=Src2Dst}"  />
          </CheckBox.Template>
        </CheckBox>
        <Button width="120" height="30">
          <Button.Template>
            <Image imageType="Slice" sprite="default.button" />
            <Text fontSize="18" height="30" width="30"  color="#000000" text="{Binding Owner content}"  />
          </Button.Template>
        </Button>
      </StackLayout>
  """
  val testXml2 = """
    <StackLayout orientation="Hor" spacing="10" padding="20,0,0,0">
       <Text fontSize="16"  ver="Start" height="30" width="30"  color="#000000" text="场景"  />
       <Text fontSize="16"  ver="Start" height="30" width="30"  color="#000000" text="项目"  />
    </StackLayout>
    
  """

  def main(args: Array[String]): Unit = {
    Assembly.add[BaseLayout]();
    Assembly.add[CheckBox]()
    Assembly.add[StackLayout]();
    Assembly.add[Image]()
    Assembly.add[BoolAtlasSprite]();
    Assembly.add[Button]()
    Assembly.add[TestViewModel]()
    Assembly.add[Text]()
    
    
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

  var frameIndex = 0;
  var testViewMode = new TestViewModel();
  def OnStart() = {
    val canvas = ui.UICanvas.create();
    Atlas.load("default","ui/default.json").get
    ui.Font.load("default","ui/WenQuanYiMicroHei.ttf",true).get
    val bgSprite = Atlas.getPath("default.white").get;
    val sprite2 = Atlas.getPath("default.button").get;
    sprite2.sliceInfo = Some(Thickness(5,5,5,5));
  
    val image = Image();
    image.sprite = Some(bgSprite)
    image.color = Color.formHex("#e8e8e7").get;    
    canvas.addControl(image);

    XmlControl.fromString(Main.testXml) match {
      case Success(uiControl) => {
        uiControl.dataContext = this.testViewMode;
        canvas.addControl(uiControl)
      }
      case Failure(exception) => println(exception);
    }
  }

  var index = 0;
  def OnUpdate() = {
    //this.testViewMode.testText = "Inc:" + index.toString();
    //index += 1;
  }
}

import ui.binding.INotifyPropertyChanged;
class TestViewModel extends INotifyPropertyChanged derives ReflectType {
  var _isTest:Boolean = true;
  def isTest = this._isTest;
  def isTest_=(value:Boolean) = {
    this._isTest = value;
    this.callPropertyChanged("isTest",this);
  }

  var _testText = "TestViewModel";

  def testText = this._testText;
  def testText_=(value:String) = {
    this._testText = value;
    this.callPropertyChanged("testText",this);
  }
}