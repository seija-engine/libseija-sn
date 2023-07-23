package ui.core

import core.RawComponent
import core.Entity
import core.RawComponentBuilder
import ui.core.FFISeijaUI

class UISystem


class UISystemBuilder extends RawComponentBuilder {
  override def build(entity: Entity): Unit = {
    FFISeijaUI.entityAddUISystem(core.App.worldPtr,entity.id)
  }
}


given UISystemComponent:RawComponent[UISystem] with {
  type BuilderType = UISystemBuilder;
  override def builder(): BuilderType = new UISystemBuilder()

  override def getRaw(entity: Entity,isMut:Boolean): RawType = ???
}