package ui

import _root_.core.IModule

import scalanative.unsafe.*
import core.{FFISeijaUI, SizeValue}
import _root_.core.reflect.DynTypeConv
import _root_.core.reflect.Assembly
import _root_.core.{Entity, FFISeijaCore}

import scala.annotation.StaticAnnotation
import ui.controls.Image
import ui.core.SizeValue.*
import ui.event.EventManager

import scala.collection.mutable;

case class ContentProperty(val name:String) extends StaticAnnotation;

final case class UIModule() extends IModule {
    def OnAdd(appPtr: Ptr[Byte]): Unit = {
        FFISeijaUI.addSpriteSheetModule(appPtr);
        FFISeijaUI.addUIModule(appPtr);
        DynTypeConv.scanPackage(ui.controls.Image);
        DynTypeConv.scanPackage(ui.Atlas);
        DynTypeConv.scanPackage(ui.core.Thickness);
        DynTypeConv.scanPackage(ui.command.FCommand);
        DynTypeConv.register[scala.Float,SizeValue];
        Assembly.scanPackage(ui.controls.Image);
        Assembly.scanPackage(ui.resources.UIResource);
        Assembly.scanPackage(ui.visualState.VisualState);
    }

    override def updateECSPtr(worldPtr: Ptr[CSignedChar]): Unit = {
      LayoutUtils.init(worldPtr)
    }

    override def update(): Unit = {
        EventManager.update();
    }
}

object UIModule {
  private def OnPostUILayout(step:Int,vec_ptr:Ptr[Byte]):Unit = {
    println(s"Post 111111 ${step}");
    if(step == 0) {
      val id = UICanvas.fst().getEntity().get.id;
      println(s"${vec_ptr} Scala ${id}")
      FFISeijaUI.vecAddU64(vec_ptr,id)
    }
  }
}