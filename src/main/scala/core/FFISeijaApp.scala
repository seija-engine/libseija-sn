package core
import scalanative.unsafe._
import ffi.LibSeija;
object FFISeijaApp {
    private val appNewPtr = LibSeija.getFunc[CFuncPtr0[Ptr[Byte]]]("app_new");
    private val appRunPtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Unit]]("app_run");
    private val appStartPtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Unit]]("app_start");
    private val appSetFPSPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],CUnsignedInt,Unit]]("app_set_fps");

    def appNew():Ptr[Byte] =  appNewPtr() 
    def appRun(appPtr:Ptr[Byte]) =  appRunPtr(appPtr)
    def appStart(appPtr:Ptr[Byte]) =  appStartPtr(appPtr) 
    def appSetFPS(appPtr:Ptr[Byte],fps:CUnsignedInt) = appSetFPSPtr(appPtr,fps) 
}
