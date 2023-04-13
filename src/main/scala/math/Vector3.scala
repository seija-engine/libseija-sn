package math
import scalanative.unsafe.{Ptr,CStruct3,CStruct4}

case class Vector3(val x:Float,val y:Float,val z:Float) {
    def +(other:Vector3):Vector3 = Vector3(x + other.x,y + other.y,z + other.z)
    
    def normalize():Vector3 = Vector3.Normalize(this)

    def length():Float = Math.sqrt(x * x + y * y + z * z).toFloat

    def mulScalar(other:Float):Vector3 = Vector3(x * other,y * other,z * other)

    def toPtr(ptr:Ptr[CStruct3[Float,Float,Float]]) = {
        ptr._1 = x
        ptr._2 = y
        ptr._3 = z
    }
    def toPtr4(ptr:Ptr[CStruct4[Float,Float,Float,Float]]) = {
        ptr._1 = x
        ptr._2 = y
        ptr._3 = z
        ptr._4 = 0
    }
}


object Vector3 {
    final val zero = new Vector3(0,0,0);
    final val one = new Vector3(1,1,1);
    def Normalize(v:Vector3):Vector3 = {
        
        v
    }
}