package core
import scalanative.unsafe._
import ffi.LibSeija;
object FFISeijaCore {
    private val addCoreModulePtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Unit]]("core_add_module");

    def addCoreModule(appPtr:Ptr[Byte]):Unit = {
        addCoreModulePtr(appPtr)
    }
}
