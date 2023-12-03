package com.seija.ui.core

import com.seija.core.RawComponentBuilder
import com.seija.core.Entity
import com.seija.ui.core.FFISeijaUI
import com.seija.core.RawComponent
import scala.scalanative.unsafe.Ptr

class ItemLayout

class ItemLayoutBuilder extends RawComponentBuilder {
  var common:CommonView = CommonView()
  override def build(entity: Entity): Unit = {
    FFISeijaUI.entityAddCommonView(com.seija.core.App.worldPtr,entity.id,common) 
  }
}

case class RawItemLayout(val rawPtr:Ptr[RawCommonView]) {
  def setWidth(value:SizeValue): Unit = {
     value match
      case SizeValue.Auto => FFISeijaUI.SetLayoutW(rawPtr,0,0)
      case SizeValue.FormRect => FFISeijaUI.SetLayoutW(rawPtr,1,0)
      case SizeValue.Pixel(v) => FFISeijaUI.SetLayoutW(rawPtr,2,v)
  }

  def setHeight(value:SizeValue): Unit = {
     value match
      case SizeValue.Auto => FFISeijaUI.SetLayoutH(rawPtr,0,0)
      case SizeValue.FormRect => FFISeijaUI.SetLayoutH(rawPtr,1,0)
      case SizeValue.Pixel(v) => FFISeijaUI.SetLayoutH(rawPtr,2,v)
  }
}

object ItemLayout {
  given ItemLayoutComponent:RawComponent[ItemLayout] with {
    type BuilderType = ItemLayoutBuilder;
    type RawType = RawItemLayout;
    override def builder(): BuilderType = new ItemLayoutBuilder()

    override def getRaw(entity: Entity,isMut:Boolean): RawType = {
      val commonViewPtr = FFISeijaUI.entityGetCommonView(com.seija.core.App.worldPtr,entity.id);
      RawItemLayout(commonViewPtr)
    }


  }
}


