package core
import core.libloading.Library
import scalanative.unsafe._
object LibSeija {
   val library = Library.New("./liblib_seija.so").left.map(code => new Throwable(s"load lib error:${code}")).toTry.get

   def getFunc[T <: CFuncPtr](name:String)(implicit tag: Tag.CFuncPtrTag[T]):T = {
      library.get[T](name).left.map(errCode => new Throwable(s"get func ${name} error:${errCode}")).toTry.get
   }
}

trait RawFFI[T,P] {
   def toRaw(value:T,ptr:P):Unit
}