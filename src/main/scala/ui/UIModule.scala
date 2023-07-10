package ui

import _root_.core.IModule

import scalanative.unsafe.*
import core.{FFISeijaUI, SizeValue}
import _root_.core.reflect.DynTypeConv
import _root_.core.reflect.Assembly
import _root_.core.{Entity,FFISeijaCore}

import scala.annotation.StaticAnnotation
import ui.controls.Image
import ui.core.SizeValue.*

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
      FFISeijaUI.SetOnPostLayout(worldPtr,CFuncPtr.toPtr(CFuncPtr1.fromScalaFunction(UIModule.OnPostUILayout)))
    }

    override def update(): Unit = {
        EventManager.update();
    }
}

object UIModule {
  private val postLayoutCallDict:mutable.HashMap[Entity,() => Unit] = mutable.HashMap.empty;
  def addPostLayoutCall(entity:Entity,callBack:() => Unit):Unit = {
    this.postLayoutCallDict.put(entity,callBack)
  }

  def removePostLayoutCall(entity:Entity):Unit = {
    this.postLayoutCallDict.remove(entity)
  }

  private def OnPostUILayout(_ptr:Ptr[Byte]):Unit = {
    val curFrame = _root_.core.Time.getFrameCount();
    for((entity,callFn) <- this.postLayoutCallDict) {
      if(FFISeijaCore.isFrameDirty(entity,curFrame)) {
        callFn()
        println(s"dirty layout ${entity} = ${curFrame}");
      }
    }
  }
}