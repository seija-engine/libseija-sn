import asset.AssetModule
import core.{CoreModule, FFISeijaCore}
import input.InputModule
import ruv.RUVModule
import transform.TransformModule
import ui.UIModule
import window.WindowModule
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import sxml.vm.VMValue
import scala.io.Source

object Main {
  def main(args: Array[String]): Unit = {
    println(s"run main")
   
    val vm = sxml.vm.SXmlVM()
    vm.addSearchPath("example/sxmltest/")
    val retValue = vm.callFile("example/sxmltest/testImport.clj").get
    println(s"eval:${retValue}")
    //runSeija()
    
  }

  def runSeija() = {
    val app = core.App;
    FFISeijaCore.initLog("ERROR");
    app.addModule(CoreModule());
    app.addModule(AssetModule("example/assets"));
    app.addModule(TransformModule());
    app.addModule(WindowModule());
    app.addModule(RUVModule());
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
    app.start(new TestDemo());
    app.run();
   
  }
}
