package com.seija.input
import scala.scalanative.unsafe._
import com.seija.math.Vector3
import com.seija.math.Vector2
import com.seija.math.RawVector2;

type RawInput = CStruct3[RawVector2,RawVector2,Boolean];

object MouseButton {
    val Left = 0
    val Right = 1
    val Middle = 2
}

object Input {
    private[input] var ptr:Ptr[Byte] = null;

    def getKeyDown(key:Int):Boolean = {
        FFISeijaInput.inputGetKeyDown(Input.ptr,key)
    }

    def getKeyUp(key:Int):Boolean = {
        FFISeijaInput.inputGetKeyUp(Input.ptr,key)
    }

    def getMoveDelta():Vector2 = {
        val inputPtr = ptr.asInstanceOf[Ptr[RawInput]];
        val x = inputPtr._2._1;
        val y = inputPtr._2._2;
        Vector2(x,y)
    }

    def getMousePos():Vector2 = {
        val inputPtr = ptr.asInstanceOf[Ptr[RawInput]];
        val x = inputPtr._1._1;
        val y = inputPtr._1._2;
        Vector2(x,y)
    }

    def getMouseDown(key:Int):Boolean = FFISeijaInput.inputGetMouseDown(Input.ptr,key)

    def getMouseUp(key:Int):Boolean = FFISeijaInput.inputGetMouseUp(Input.ptr,key)
}
