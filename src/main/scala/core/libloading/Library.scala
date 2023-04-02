package core
package libloading
import scalanative.windows.HandleApi._
import scalanative.unsafe._

class Library(dllPtr:Handle) {

    def get[T <: CFuncPtr](symbolName:String)(implicit tag: Tag.CFuncPtrTag[T]):Either[Long,T] = {
        Zone { implicit z =>
            val errCode = stackalloc[CUnsignedLong]()
            val cSymName = toCString(symbolName)
            val ptr = slib.dll_get_sym(dllPtr,cSymName,errCode);
            
            if((!errCode).toLong == 0L) {
                val funcPtr = CFuncPtr.fromPtr[T](ptr);
                Right(funcPtr)
            } else {
                Left((!errCode).toLong)
            }
        }
        
    }

}

object Library {
    def New(libPath:String):Either[Long,Library] = {
        Zone { implicit z =>
            val errCode = stackalloc[CUnsignedLong]()
            val cLibPath = toCString(libPath)
            val handle = slib.load_dll(cLibPath,errCode);
            if((!errCode).toLong == 0L) {
                Right(new Library(handle))
            } else {
                Left((!errCode).toLong)
            }
        }
    }
}
