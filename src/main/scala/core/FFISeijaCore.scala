package core
import scalanative.unsafe._
object FFISeijaCore {
    private val addCoreModulePtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Unit]]("core_add_module");
    private val appSetOnStartPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Ptr[Byte],Unit]]("app_set_on_start");
    private val appSetOnUpdatePtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Ptr[Byte],Unit]]("app_set_on_update");

    def addCoreModule(appPtr:Ptr[Byte]):Unit = {
        addCoreModulePtr(appPtr)
    }

    def appSetOnStart(appPtr:Ptr[Byte], func:Ptr[Byte]):Unit = {
        appSetOnStartPtr(appPtr,func)
    }

    def appSetOnUpdate(appPtr:Ptr[Byte], func:Ptr[Byte]):Unit = {
        appSetOnUpdatePtr(appPtr,func)
    }
}
