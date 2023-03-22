package transform

import core.IModule

class TransformModule extends IModule {

  override def OnAdd(app: core.App): Unit = {
    FFISeijaTransform.addTransformModule(app.ptr)
  }

    
}
