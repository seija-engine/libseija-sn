import core.{IGameApp, logError}
import core.reflect.Assembly
import ui.Atlas
import ui.binding.{INotifyPropertyChanged, ObservableList}
import ui.command.FCommand
import ui.core.Thickness
import ui.resources.UIResourceMgr
import ui.xml.XmlUIElement

class TestDemo extends IGameApp {
  var testViewModel:Option[TestViewModel] = None;
  def loadAsset(): Unit = {
    ui.CanvasManager.init()
    Atlas.load("default","ui/default.json").get
    ui.Font.load("default","ui/WenQuanYiMicroHei.ttf",true).get
    Atlas.getPath("default.button").get.sliceInfo = Some(Thickness(5,5,5,5));
    Atlas.getPath("default.scale-vert-trough").get.sliceInfo = Some(Thickness(0,10,0,10));
    Atlas.getPath("default.scale-horz-trough").get.sliceInfo = Some(Thickness(10,0,10,0));
    Atlas.getPath("default.scale-horz-trough-active").get.sliceInfo = Some(Thickness(10,0,10,0));
    Atlas.getPath("default.scale-vert-trough-active").get.sliceInfo = Some(Thickness(0,10,0,10));

    Atlas.getPath("default.scrollbar-vert-slider").get.sliceInfo = Some(Thickness(0,5,0,5));
    Atlas.getPath("default.scrollbar-vert-slider-active").get.sliceInfo = Some(Thickness(0,5,0,5));
    Atlas.getPath("default.scrollbar-vert-slider-hover").get.sliceInfo = Some(Thickness(0,5,0,5));

    Atlas.getPath("default.scrollbar-horz-slider").get.sliceInfo = Some(Thickness(5,0,5,0));
    Atlas.getPath("default.scrollbar-horz-slider-active").get.sliceInfo = Some(Thickness(5,0,5,0));
    Atlas.getPath("default.scrollbar-horz-slider-hover").get.sliceInfo = Some(Thickness(5,0,5,0));
    Atlas.getPath("default.scrollbar-vert-trough").get.sliceInfo = Some(Thickness(0,0,1,0));
    Atlas.getPath("default.scrollbar-horz-trough").get.sliceInfo = Some(Thickness(0,0,0,1));

    Atlas.getPath("default.frame").get.sliceInfo = Some(Thickness(1,1,1,1));
    Atlas.getPath("default.menu-border").get.sliceInfo = Some(Thickness(1,1,1,1));
  }
  
  override def OnStart(): Unit = {
    Assembly.add[TestViewModel]();
    this.loadAsset()

    UIResourceMgr.loadScriptResource("example/assets/ui/AppStyle.clj")
    
    val viewModel = new TestViewModel();
    this.testViewModel = Some(viewModel);
    XmlUIElement.fromFile("example/assets/ui/xmltest/testList.xml").logError().foreach {loadElement =>
      loadElement.addIDScope();
      loadElement.dataContext = this.testViewModel.get;
      ui.CanvasManager.fst().addElement(loadElement)
    }
}

  override def OnUpdate(): Unit = {
    //val dt = Time.getDeltaTime();
    //this.testViewModel.get.setTestString(s"dt:${dt.formatted("%.3f")}  frame:${Time.getFrameCount()}");
  }
}

import core.reflect.ReflectType;  
class TestViewModel extends INotifyPropertyChanged derives ReflectType {
    var _floatNumber:Float = 0
    var count:Int = 0;
    var numCommand:FCommand = FCommand(this.testClick);

    def floatNumber: Float = this._floatNumber
    def floatNumber_=(value:Float):Unit = {
      this._floatNumber = value
      callPropertyChanged("floatNumber",this)
    }

    var lstCommand:FCommand = FCommand(this.testAdd);
    var insertCommand:FCommand = FCommand(this.testInsert);
    var updateCommand:FCommand = FCommand(this.testUpdate);
    var removeCommand:FCommand = FCommand(this.testRemove);
    var moveCommand:FCommand = FCommand(this.testMove);
    var clearCommand:FCommand = FCommand(this.testClear);
    var dataList:ObservableList[String] = ObservableList.from(List(
      "Data@1",
      "Data@2",
      "Data@3","Data@4","Data@5"));
    var newCommand:FCommand = FCommand(this.testNew)

    def setCount(count: Int): Unit = {
      this.count = count;
      this.callPropertyChanged("count", this);
    }

    def testAdd(params:Any):Unit = {
       println("test add")
       this.dataList.add(s"Data@${this.dataList.length + 1}");
    }

    def testInsert(params:Any):Unit = {
       if(this.dataList.length >= 1) {
          this.dataList.insert(1,"Insert#1");
       }
    }

    def testUpdate(params:Any):Unit = {
      if(this.dataList.length > 0) {
        this.dataList.update(0,"Replace 0");
      }
    }

    def testRemove(params:Any):Unit = {
       if(this.dataList.length > 0) {
         this.dataList.removeAt(0);
       }
    }

    def testMove(params:Any):Unit = {
       if(this.dataList.length < 2) return;
       this.dataList.move(0,3);
    }

    def testClear(params:Any):Unit = {
      this.dataList.clear();
    }

    def testClick(params:Any):Unit = {
       params match
        case "+" => this.setCount(this.count + 1)
        case "-" => this.setCount(this.count - 1)
        case _ =>
    }

    def testNew(params:Any):Unit = {
      slog.info("On New Menu")
    }
}