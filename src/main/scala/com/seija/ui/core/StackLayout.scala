package com.seija.ui.core

import com.seija.core.RawComponentBuilder
import com.seija.core.Entity
import com.seija.ui.core.FFISeijaUI
import com.seija.core.RawComponent
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

object StackLayout {
    given StackLayoutComponent:RawComponent[StackLayout] with {
    type BuilderType = StackLayoutBuilder;
    type RawType = RawStackLayout
    override def builder(): BuilderType = new StackLayoutBuilder()

    override def getRaw(entity: Entity,isMut:Boolean): RawType = {
      val stack = FFISeijaUI.entityGetStackView(com.seija.core.App.worldPtr,entity.id);
      val commonView = FFISeijaUI.entityGetCommonView(com.seija.core.App.worldPtr,entity.id);
      RawStackLayout(commonView,stack)
    } 
  }
}

class StackLayoutBuilder extends RawComponentBuilder {
  var common:CommonView = CommonView()
  var spacing:Float = 0
  var orientation:Orientation = Orientation.Horizontal;
  override def build(entity: Entity): Unit = {
     FFISeijaUI.entityAddStack(com.seija.core.App.worldPtr,entity.id,spacing,orientation.v,common);
  }
}


