package com.seija.ui
import com.seija.asset.AssetModule
import com.seija.core
import com.seija.core.{CoreModule, IGameApp}
import com.seija.input.InputModule
import com.seija.ruv.RUVModule
import com.seija.transform.TransformModule
import com.seija.window.WindowModule
class UIApp extends IGameApp{
  val app: core.App.type = core.App
  protected val winModule:WindowModule = new WindowModule
  
  def init():Unit = {}
  override def OnStart(): Unit = {
    com.seija.ui.CanvasManager.init()  
  }

  override def OnUpdate(): Unit = {

  }
  def run():Unit = {
      this.init()
      app.addModule(CoreModule())
      app.addModule(AssetModule("assets"))
      app.addModule(TransformModule())
      app.addModule(this.winModule)
      app.addModule(RUVModule())
      app.addModule(InputModule())
      app.addModule(
        com.seija.render.RenderModule(
          com.seija.render.RenderConfig(
            "assets/.shader",
            "assets/script/render.clj",
            List("assets/script")
          )
        )
      );
      app.addModule(UIModule())
      app.start(this)
      app.run()
  }
}