package math
import scalanative.unsafe.{Ptr,CStruct3}

case class Vector3(val x:Float,val y:Float,val z:Float) {
    def +(other:Vector3):Vector3 = Vector3(x + other.x,y + other.y,z + other.z)
    
    def normalize():Vector3 = Vector3.Normalize(this)
    def length():Float = Math.sqrt(x * x + y * y + z * z).toFloat

    def toPtr(ptr:Ptr[CStruct3[Float,Float,Float]]) = {
        ptr._1 = x
        ptr._2 = y
        ptr._3 = z
    }
}


object Vector3 {
    final val zero = new Vector3(0,0,0);
    
    def Normalize(v:Vector3):Vector3 = {
        
        v
    }
}