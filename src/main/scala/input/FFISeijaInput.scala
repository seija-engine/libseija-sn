package input
import scala.scalanative.unsafe._
import core.LibSeija;

object FFISeijaInput {
    private val addInputModulePtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Unit]]("input_add_module");
    private val worldGetInputPtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Ptr[Byte]]]("input_world_get_input");
    def addInputModule(appPtr: Ptr[Byte]): Unit = {
        addInputModulePtr(appPtr);
    }

    def worldGetInput(worldPtr: Ptr[Byte]): Ptr[Byte] = {
        worldGetInputPtr(worldPtr);
    }
}
