import asset.AssetModule
import core.{CoreModule, FFISeijaCore}
import input.InputModule
import ruv.RUVModule
import transform.TransformModule
import ui.UIModule
import window.WindowModule
import scala.io.AnsiColor
import math.Color
object Main {
  def main(args: Array[String]): Unit = {
    slog.info("run main")
    runSeija()
  }

  def runSeija(): Unit = {
    val app = core.App
    FFISeijaCore.initLog("ERROR")
    app.addModule(CoreModule())
    app.addModule(AssetModule("example/assets"))
    app.addModule(TransformModule())
    app.addModule(WindowModule())
    app.addModule(RUVModule())
    app.addModule(InputModule())
    app.addModule(
      render.RenderModule(
        render.RenderConfig(
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
