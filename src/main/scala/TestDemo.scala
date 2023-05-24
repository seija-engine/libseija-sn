import core.IGameApp
import ui.Atlas
import ui.core.Thickness
import ui.UICanvas

class TestDemo extends IGameApp {
  var topCanvas:Option[UICanvas] = None;
  def loadAsset() = {
    val canvas = ui.UICanvas.create();
    Atlas.load("default","ui/default.json").get
    ui.Font.load("default","ui/WenQuanYiMicroHei.ttf",true).get
    Atlas.getPath("default.button").get.sliceInfo = Some(Thickness(5,5,5,5));
    this.topCanvas = Some(canvas);
  }
  
  override def OnStart(): Unit = {
    this.loadAsset();
    
  }

  override def OnUpdate(): Unit = {
    
  }


}