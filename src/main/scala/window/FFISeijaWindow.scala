package window
import scalanative.unsafe._
import core.LibSeija;
type CWindowConfig = CStruct4[CFloat,CFloat,CInt,CBool];

object FFISeijaWindow {
  private val newConfigPtr = LibSeija.getFunc[CFuncPtr0[Ptr[CWindowConfig]]]("winit_new_windowconfig");

  private val setTitlePtr = LibSeija.getFunc[CFuncPtr2[Ptr[CWindowConfig],CString,Unit]]("winit_windowconfig_set_title");

  private val addWinitModulePtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Ptr[CWindowConfig],Unit]]("winit_add_module");

  def addWinitModule(appPtr:Ptr[Byte],config:Ptr[CWindowConfig]):Unit = {
      addWinitModulePtr(appPtr,config)
  }

  def newWindowConfig():Ptr[CWindowConfig] = newConfigPtr()

  def SetConfigTitle(ptr:Ptr[CWindowConfig],title:String) = Zone { implicit z =>
      setTitlePtr(ptr,toCString(title))
  }
}
