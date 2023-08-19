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

object Main {
  def main(args: Array[String]): Unit = {
    println(s"run main")
    val fs = scala.io.Source.fromFile("example/sxmltest/test7.clj")
    val parser = sxml.parser.Parser.fromSource("test7.clj",fs)
    val astModule = parser.parseModule().get
    val trans = sxml.compiler.Translator()
    val transModule = trans.translateModule(astModule).get
    //println(transModule)
    val compiler = sxml.compiler.Compiler()
    val module = compiler.compileModule(transModule).get
    module.function.debugShow(0)
    val vm = sxml.vm.SXmlVM()
    val retValue = vm.runModule(module).get
    //println(s"eval:${retValue}")
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
