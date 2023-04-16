package ui

import core.Entity
import core.RawComponentBuilder
import core.RawComponent

class UICanvas

class UICanvasBuilder extends RawComponentBuilder {
  override def build(entity: Entity):Unit = {
    FFISeijaUI.entityAddUICanvas(core.App.worldPtr,entity.id);
  }
}

given UICanvasComponent:RawComponent[UICanvas] with {
  type BuilderType = UICanvasBuilder;
  type RawType = UICanvas;

  override def builder(): BuilderType = new UICanvasBuilder()

  override def getRaw(entity: Entity): RawType = ???  
}