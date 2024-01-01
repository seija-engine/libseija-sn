import com.seija.core.{IGameApp, logError}
import com.seija.core.reflect.Assembly
import com.seija.ui.Atlas
import com.seija.ui.binding.{INotifyPropertyChanged, ObservableList}
import com.seija.ui.command.FCommand
import com.seija.ui.core.Thickness
import com.seija.ui.resources.UIResourceMgr
import com.seija.ui.xml.XmlUIElement
import com.seija.ui.CanvasManager
import com.seija.input.Input
import com.seija.input.KeyCode

class TestDemo extends IGameApp {
  var testViewModel:Option[TestViewModel] = None;
  def loadAsset(): Unit = {
    com.seija.ui.CanvasManager.init()
    Atlas.load("default","ui/default.json").get
    com.seija.ui.Font.load("default","ui/WenQuanYiMicroHei.ttf",true).get
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
    Atlas.getPath("default.entry").get.sliceInfo = Some(Thickness(4,4,4,4));
    Atlas.getPath("default.entry-active").get.sliceInfo = Some(Thickness(4,4,4,4));
    Atlas.getPath("default.entry-insensitive").get.sliceInfo = Some(Thickness(4,4,4,4));
  }
  
  override def OnStart(): Unit = {
    Assembly.add[TestViewModel]();
    Assembly.add[TestDataItem]()
    this.loadAsset()

    UIResourceMgr.loadScriptResource("example/assets/ui/AppStyle.clj")
    
    val viewModel = new TestViewModel();
    this.testViewModel = Some(viewModel);
    XmlUIElement.fromFile("example/assets/ui/xmltest/testFlex.xml").logError().foreach {loadElement =>
      loadElement.addIDScope();
      loadElement.dataContext = this.testViewModel.get;
      
      
      com.seija.ui.CanvasManager.fst().addElement(loadElement)
    }
}

  override def OnUpdate(): Unit = {
    if(Input.getKeyDown(KeyCode.Escape)) {
      println("call quit?")
      com.seija.core.App.quit()
    }
    //val dt = Time.getDeltaTime();
    //this.testViewModel.get.setTestString(s"dt:${dt.formatted("%.3f")}  frame:${Time.getFrameCount()}");
  }
}

import com.seija.core.reflect.ReflectType;  

class TestDataItem extends INotifyPropertyChanged derives ReflectType {
  var _ID:Int = 0
  var _Name:String = "DataItem";
  def ID = this._ID
  def ID_=(value:Int) = { 
      this._ID = value
      callPropertyChanged("ID")
  }
  def Name = this._Name
  def Name_=(value:String) = { 
      this._Name = value
      callPropertyChanged("Name")
  }
}

class TestViewModel extends INotifyPropertyChanged derives ReflectType {
    var _floatNumber:Float = 0
    var count:Int = 0;
    var _testString:String = "TestString";
    def testString = this._testString
    def testString_=(value:String):Unit = { this._testString = value; callPropertyChanged("testString") }
    var numCommand:FCommand = FCommand(this.testClick);

    def floatNumber: Float = this._floatNumber
    def floatNumber_=(value:Float):Unit = {
      this._floatNumber = value
      callPropertyChanged("floatNumber")
    }

    var lstCommand:FCommand = FCommand(this.testAdd);
    var insertCommand:FCommand = FCommand(this.testInsert);
    var updateCommand:FCommand = FCommand(this.testUpdate);
    var removeCommand:FCommand = FCommand(this.testRemove);
    var moveCommand:FCommand = FCommand(this.testMove);
    var clearCommand:FCommand = FCommand(this.testClear);
    var openDialogCommand:FCommand = FCommand(this.openDialog);
    var update2Command:FCommand = FCommand(this.testUpdate2);
    var setStringCommand:FCommand = FCommand(this.setString);

    var dataList:ObservableList[String] = ObservableList.from(List(
      "Data@1",
      "Data@2",
      "Data@3","Data@4","Data@5"));
    var newCommand:FCommand = FCommand(this.testNew)

    var dataList2:ObservableList[TestDataItem] = ObservableList.from(List(new TestDataItem(),new TestDataItem()))

    def setCount(count: Int): Unit = {
      this.count = count;
      this.callPropertyChanged("count");
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

    def testUpdate2(params:Any):Unit = {
      this.dataList2.apply(1).Name = this.dataList2.apply(1).Name + "11"
    }

    def setString(params:Any):Unit = {
      this.testString = "重置文本"
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

    def openDialog(params:Any):Unit = {
      slog.error("Open Dialog")
      val newDialog = XmlUIElement.fromFile("example/assets/ui/xmltest/dialog.xml").logError().get
      CanvasManager.popup().addElement(newDialog);
    }

    override def onPropertyChanged(propertyName: String): Unit = {
      propertyName match
        case "testString" => 
          println(s"new testString:${this._testString}")
        case _ =>
      
    }
}