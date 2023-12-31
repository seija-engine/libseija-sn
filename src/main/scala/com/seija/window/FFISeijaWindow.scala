package com.seija.window
import scalanative.unsafe._
import com.seija.core.LibSeija;
type CWindowConfig = CStruct4[CFloat,CFloat,CInt,CBool];

object FFISeijaWindow {
  private val newConfigPtr = LibSeija.getFunc[CFuncPtr0[Ptr[CWindowConfig]]]("winit_new_windowconfig");
  private val setTitlePtr = LibSeija.getFunc[CFuncPtr2[Ptr[CWindowConfig],CString,Unit]]("winit_windowconfig_set_title");
  private val addWinitModulePtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Ptr[CWindowConfig],Unit]]("winit_add_module");

  private val set_window_fullscreenPtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Unit]]("set_window_fullscreen");
  private val set_maximizedPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Boolean,Unit]]("set_maximized");
  private val set_inner_sizePtr = LibSeija.getFunc[CFuncPtr3[Ptr[Byte],Float,Float,Unit]]("set_inner_size");

  def addWinitModule(appPtr:Ptr[Byte],config:Ptr[CWindowConfig]):Unit = {
      addWinitModulePtr(appPtr,config)
  }

  def newWindowConfig():Ptr[CWindowConfig] = newConfigPtr()

  def SetConfigTitle(ptr:Ptr[CWindowConfig],title:String) = Zone { implicit z =>
      setTitlePtr(ptr,toCString(title))
  }

  def setFullScreen(ptr:Ptr[Byte]):Unit = set_window_fullscreenPtr(ptr)
  def setMaximized(ptr:Ptr[Byte],value:Boolean):Unit = set_maximizedPtr(ptr,value)
  def setInnerSize(ptr:Ptr[Byte],w:Float,h:Float):Unit = set_inner_sizePtr(ptr,w,h)
}
