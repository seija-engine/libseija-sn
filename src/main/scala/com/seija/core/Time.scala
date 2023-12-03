package com.seija.core
import scala.scalanative.unsafe.Ptr
import scala.scalanative.unsigned.ULong

object Time {
    var timePtr:Ptr[RawTime] = null;
    def getDeltaTime():Float = timePtr._1
    def getFrameCount():ULong = timePtr._2
    def Frame:Long = this.getFrameCount().toLong
}