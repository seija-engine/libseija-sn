package core
package libloading
import scala.scalanative.unsafe._
import scalanative.windows.HandleApi._

@extern
object slib {
    def load_dll(dllPath:CString,errorCode:Ptr[CUnsignedLong]):Ptr[Byte] = extern;

    def dll_get_sym(dllHandle:Ptr[Byte],symName:CString,errorCode:Ptr[CUnsignedLong]):Ptr[Byte] = extern;
}
