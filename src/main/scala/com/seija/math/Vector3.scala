package com.seija.math
import scalanative.unsafe.{CFloat, CStruct3, CStruct4, Ptr}

type RawVector3 = CStruct3[CFloat,CFloat,CFloat]

case class Vector3(var x:Float,var y:Float,var z:Float) {
    def +(other:Vector3):Vector3 = Vector3(x + other.x,y + other.y,z + other.z)
    
    def normalize():Vector3 = Vector3.Normalize(this)

    def length():Float = Math.sqrt(x * x + y * y + z * z).toFloat

    def mulScalar(other:Float):Vector3 = Vector3(x * other,y * other,z * other)

    def setToPtr(ptr: Ptr[RawVector3]): Unit = {
      ptr._1 = x
      ptr._2 = y
      ptr._3 = z
    }
    def setToPtr4(ptr:Ptr[CStruct4[Float,Float,Float,Float]]) = {
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