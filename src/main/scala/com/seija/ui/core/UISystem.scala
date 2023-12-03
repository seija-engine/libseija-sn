package com.seija.ui.core

import com.seija.core.RawComponent
import com.seija.core.Entity
import com.seija.core.RawComponentBuilder
import com.seija.ui.core.FFISeijaUI

class UISystem


class UISystemBuilder extends RawComponentBuilder {
  override def build(entity: Entity): Unit = {
    FFISeijaUI.entityAddUISystem(com.seija.core.App.worldPtr,entity.id)
  }
}


given UISystemComponent:RawComponent[UISystem] with {
  type BuilderType = UISystemBuilder;
  override def builder(): BuilderType = new UISystemBuilder()

  override def getRaw(entity: Entity,isMut:Boolean): RawType = ???
}