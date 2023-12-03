package com.seija.math

import scala.scalanative.unsafe.{Ptr,CStruct4}

case class Quat(var x:Float,val y:Float,val z:Float,val w:Float) {
    def toPtr(ptr:Ptr[CStruct4[Float,Float,Float,Float]]) = {
        ptr._1 = x
        ptr._2 = y
        ptr._3 = z
        ptr._4 = w
    }
}

object Quat {
    val identity = new Quat(0,0,0,1)

    def fromXYZW(x:Float,y:Float,z:Float,w:Float):Quat = new Quat(x,y,z,w)

    def fromAxisAngle(axis:Vector3,angle:Float):Quat = {
        val halfAngle = angle * 0.5;
        val s = Math.sin(halfAngle).asInstanceOf[Float];
        val c = Math.cos(halfAngle).asInstanceOf[Float];
        val v = axis.mulScalar(s);
        new Quat(v.x,v.y,v.z,c);   
    }
}
