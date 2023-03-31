package input

import scala.scalanative.unsafe._

object Input {
    private[input] var ptr:Ptr[Byte] = null;

    def getKeyDown(key:Int):Boolean = {
        FFISeijaInput.inputGetKeyDown(Input.ptr,key)
    }

    def getKeyUp(key:Int):Boolean = {
        FFISeijaInput.inputGetKeyUp(Input.ptr,key)
    }
}
