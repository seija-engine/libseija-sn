package ui.core

import core.RawComponentBuilder
import core.Entity
import ui.core.FFISeijaUI
import core.RawComponent
import scala.scalanative.unsafe._

case class RawStackLayout(val view:Ptr[RawCommonView],val stack:CStruct2[CFloat,Byte]) {
  def setOrientation(value:Orientation) = {
    stack._2 = value.v;
  }

  def getOrientation():Orientation = {
    val byteValue:Byte = stack._2;
    Orientation.fromOrdinal(byteValue)
  }

  def setPadding(thickness:Thickness) = {
     ThicknessRawFFI.toRaw(thickness,view.at2)
  }
}

class StackLayout;

class StackLayoutBuilder extends RawComponentBuilder {
  var common:CommonView = CommonView()
  var spacing:Float = 0
  var orientation:Orientation = Orientation.Horizontal;
  override def build(entity: Entity): Unit = {
     FFISeijaUI.entityAddStack(core.App.worldPtr,entity.id,spacing,orientation.v,common);
  }
}

given StackLayoutComponent:RawComponent[StackLayout] with {
  type BuilderType = StackLayoutBuilder;
  type RawType = RawStackLayout
  override def builder(): BuilderType = new StackLayoutBuilder()

  override def getRaw(entity: Entity): RawType = {
    val stack = FFISeijaUI.entityGetStackView(core.App.worldPtr,entity.id);
    val commonView = FFISeijaUI.entityGetCommonView(core.App.worldPtr,entity.id);
    RawStackLayout(commonView,stack)
  } 
}
