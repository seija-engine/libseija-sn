import com.seija.asset.AssetModule
import com.seija.core.{CoreModule, FFISeijaCore}
import com.seija.input.InputModule
import com.seija.ruv.RUVModule
import com.seija.transform.TransformModule
import com.seija.ui.UIModule
import com.seija.window.WindowModule
import scala.io.AnsiColor
import com.seija.math.Color
import com.seija
import com.seija.`2d`.Module2D
object Main {
  def main(args: Array[String]): Unit = {
    //slog.Logger.root.clearModifiers().withMinimumLevel(slog.Level.Trace)
    slog.info("run main")
    runSeija()
  }

  def runSeija(): Unit = {
    val app = com.seija.core.App
    
    FFISeijaCore.initLog("ERROR")
    app.addModule(CoreModule())
    app.addModule(AssetModule("example/assets"))
    app.addModule(TransformModule())
    app.addModule(WindowModule())
    app.addModule(RUVModule())
    app.addModule(InputModule())
    app.addModule(Module2D())
    app.addModule(
      com.seija.render.RenderModule(
        com.seija.render.RenderConfig(
          "example/.shader",
          "example/script/render.clj",
          List("example/script")
        )
      )
    );


    app.addModule(UIModule())
    app.start(new TestDemo())
    app.run()

  }
}
