package core
package libloading
import scala.scalanative.unsafe._
import scalanative.windows.HandleApi._

@extern
object slib {
    def WinLoadLib(dllPath:CString,errorCode:Ptr[CUnsignedLong]):Handle = extern;

    def WinGetSymbol(dllHandle:Handle,symName:CString,errorCode:Ptr[CUnsignedLong]):Ptr[Byte] = extern;
}