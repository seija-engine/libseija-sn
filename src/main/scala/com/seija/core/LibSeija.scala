package com.seija.core
import com.seija.core.libloading.Library
import scalanative.unsafe._
object LibSeija {

   val library = initLib()

   def getFunc[T <: CFuncPtr](name:String)(implicit tag: Tag.CFuncPtrTag[T]):T = {
      library.get[T](name).left.map(errCode => new Throwable(s"get func ${name} error:${errCode}")).toTry.get
   }

   def initLib():Library = {
      val libPath = if(scalanative.runtime.Platform.isWindows()) {
        "lib_seija.dll"
      } else {
         "./liblib_seija.so"
      }
      Library.New(libPath).left.map(code => new Throwable(s"load lib error:${code}")).toTry.get
   }
}

trait RawFFI[T,P] {
   def toRaw(value:T,ptr:P):Unit
}