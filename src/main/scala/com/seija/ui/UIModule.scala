package com.seija.ui
import com.seija.core.IModule
import com.seija.core.reflect.{Assembly, DynTypeConv}
import com.seija.ui.controls.Image
import com.seija.ui.core.SizeValue.*
import com.seija.ui.core.{FFISeijaUI, SizeValue}
import com.seija.ui.event.EventManager
import com.seija.ui.xml.UISXmlEnv

import scala.annotation.StaticAnnotation
import scala.scalanative.unsafe.*
import com.seija.core.reflect.{DynTypeConv, Assembly}
import com.seija.core.IModule

case class ContentProperty(val name: String) extends StaticAnnotation

final case class UIModule() extends IModule {
  def OnAdd(appPtr: Ptr[Byte]): Unit = {
    UISXmlEnv.init()
    FFISeijaUI.addSpriteSheetModule(appPtr)
    FFISeijaUI.addUIModule(appPtr)
    DynTypeConv.scanPackage(com.seija.ui.controls.Image);
    DynTypeConv.scanPackage(com.seija.ui.Atlas)
    DynTypeConv.scanPackage(com.seija.ui.core.Thickness)
    DynTypeConv.scanPackage(com.seija.ui.command.FCommand)
    DynTypeConv.register[scala.Float, SizeValue]
    Assembly.scanPackage(com.seija.ui.controls.Image);
    Assembly.scanPackage(com.seija.ui.resources.UIResource);
    Assembly.scanPackage(com.seija.ui.visualState.ViewStates)
    Assembly.scanPackage(com.seija.ui.trigger.TriggerList)
  }

  override def updateECSPtr(worldPtr: Ptr[CSignedChar]): Unit = {
    LayoutUtils.init(worldPtr)
  }

  override def update(): Unit = {
    LayoutUtils.OnUpdate()
    EventManager.update()
  }
}
