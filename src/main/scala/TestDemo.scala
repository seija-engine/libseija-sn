import core.IGameApp
import ui.Atlas
import ui.core.Thickness
import ui.UICanvas
import ui.xml2.XmlUIElement
import core.reflect.Assembly
import ui.binding.INotifyPropertyChanged
import core.Time

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
    val viewModel = new TestViewModel();
    this.testViewModel = Some(viewModel);
    val loadElement = XmlUIElement.fromFile("example/assets/ui/demo.xml").get;
    loadElement.dataContext = Some(viewModel);
    this.topCanvas.get.addElement(loadElement);
  }

  override def OnUpdate(): Unit = {
    val dt = Time.getDeltaTime();
    
    this.testViewModel.get.setTestString(s"dt:${dt.formatted("%.3f")}  frame:${Time.getFrameCount()}");
  }
}

import core.reflect.ReflectType;  
class TestViewModel extends INotifyPropertyChanged derives ReflectType {
    var testString:String = "TestViewModel.testString";
    def setTestString(v:String) = {
        this.testString = v;
        this.callPropertyChanged("testString",this);
    }
}