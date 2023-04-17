package ui.core

import core.RawComponentBuilder
import core.Entity
import ui.FFISeijaUI
import core.RawComponent

class ItemLayout

class ItemLayoutBuilder extends RawComponentBuilder {
  var common:CommonView = CommonView()
  override def build(entity: Entity): Unit = {
    FFISeijaUI.entityAddCommonView(core.App.worldPtr,entity.id,common) 
  }
}

given ItemLayoutComponent:RawComponent[ItemLayout] with {
  type BuilderType = ItemLayoutBuilder;
  override def builder(): BuilderType = new ItemLayoutBuilder()

  override def getRaw(entity: Entity): RawType = ???


}
