package transform
import core.LibSeija;
import scalanative.unsafe._

object FFISeijaTransform {
  private val addTransformModulePtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Unit]]("tranrform_add_module");

    def addTransformModule(appPtr:Ptr[Byte]):Unit = {
        addTransformModulePtr(appPtr)
    }
}
