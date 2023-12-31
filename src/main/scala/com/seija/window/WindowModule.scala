package com.seija.window;
import scala.scalanative.unsafe._
import com.seija.core.IModule


class WindowModule extends IModule {
  var width: Float = 1024f

  var height: Float = 768f

  var title: String = "Seija In Scala Native"
  def OnAdd(appPtr:Ptr[Byte]):Unit = {
       val configPtr = FFISeijaWindow.newWindowConfig();
       configPtr._1 = width;
       configPtr._2 = height;
       
       FFISeijaWindow.SetConfigTitle(configPtr,title);
       FFISeijaWindow.addWinitModule(appPtr,configPtr);
  }
}


object Window {
  def setFullScreen():Unit = FFISeijaWindow.setFullScreen(com.seija.core.App.appPtr)
  def setMaximized(value:Boolean):Unit = FFISeijaWindow.setMaximized(com.seija.core.App.appPtr,value)
  def setInnerSize(width:Float,height:Float) = FFISeijaWindow.setInnerSize(com.seija.core.App.appPtr,width,height)
}