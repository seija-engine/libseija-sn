package com.seija.ui.core

import com.seija.core.Entity
import com.seija.core.RawComponentBuilder
import com.seija.core.RawComponent
import com.seija.ui.core.FFISeijaUI


class UICanvas

class UICanvasBuilder extends RawComponentBuilder {
  override def build(entity: Entity):Unit = {
    FFISeijaUI.entityAddUICanvas(com.seija.core.App.worldPtr,entity.id);
  }
}

given UICanvasComponent:RawComponent[UICanvas] with {
  type BuilderType = UICanvasBuilder;
  type RawType = UICanvas;

  override def builder(): BuilderType = new UICanvasBuilder()

  override def getRaw(entity: Entity,isMut:Boolean): RawType = ???
}