import core.IGameApp
import ui.Atlas
import ui.core.Thickness
import ui.UICanvas
import ui.xml.XmlUIElement
import core.reflect.Assembly
import ui.binding.INotifyPropertyChanged
import core.Time
import core.logError;
import ui.controls.Control
import ui.resources.UIResourceMgr
import command.FCommand

class TestDemo extends IGameApp {
  var topCanvas:Option[UICanvas] = None;
  var testViewModel:Option[TestViewModel] = None;
  def loadAsset() = {
    val canvas = ui.UICanvas.create();
    Atlas.load("default","ui/default.json").get
    ui.Font.load("default","ui/WenQuanYiMicroHei.ttf",true).get
    Atlas.getPath("default.button").get.sliceInfo = Some(Thickness(5,5,5,5));
    this.topCanvas = Some(canvas);
  }
  
  override def OnStart(): Unit = {
    Assembly.add[TestViewModel]();
    this.loadAsset();
    UIResourceMgr.loadResource("example/assets/ui/AppStyle.xml");
    val viewModel = new TestViewModel();
    this.testViewModel = Some(viewModel);
    XmlUIElement.fromFile("example/assets/ui/testStyle.xml").logError().foreach {loadElement => 
      loadElement.dataContext = this.testViewModel.get;
      this.topCanvas.get.addElement(loadElement);
    }
}

  override def OnUpdate(): Unit = {
    val dt = Time.getDeltaTime();
    
    this.testViewModel.get.setTestString(s"dt:${dt.formatted("%.3f")}  frame:${Time.getFrameCount()}");
  }
}

import core.reflect.ReflectType;  
class TestViewModel extends INotifyPropertyChanged derives ReflectType {
    var testString:String = "TestViewModel.testString";
    var testCommand:FCommand = FCommand(this.testClick);

    def setTestString(v:String) = {
        this.testString = v;
        this.callPropertyChanged("testString",this);
    }

    def testClick(params:Any):Unit = {
       println("test Click");
    }
}