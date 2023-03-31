package input
import scala.scalanative.unsafe._
import core.LibSeija;

object FFISeijaInput {
    private val addInputModulePtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Unit]]("input_add_module");
    private val worldGetInputPtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Ptr[Byte]]]("input_world_get_input");
    private val inputGetKeyDownPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Int,Boolean]]("input_get_keydown");
    private val inputGetKeyUpPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Int,Boolean]]("input_get_keyup");
    def addInputModule(appPtr: Ptr[Byte]): Unit = {
        addInputModulePtr(appPtr);
    }

    def worldGetInput(worldPtr: Ptr[Byte]): Ptr[Byte] = {
        worldGetInputPtr(worldPtr);
    }

    def inputGetKeyDown(inputPtr: Ptr[Byte], key: Int): Boolean = {
        inputGetKeyDownPtr(inputPtr, key)
    }

    def inputGetKeyUp(inputPtr: Ptr[Byte], key: Int): Boolean = {
        inputGetKeyUpPtr(inputPtr, key)
    }
}
