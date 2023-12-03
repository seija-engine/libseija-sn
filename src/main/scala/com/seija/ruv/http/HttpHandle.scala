package com.seija.ruv.http

import com.seija.ruv.FFIRuv
import com.seija.ruv.RuvRuntime
import scala.util.Try
import scala.util.Success
import scala.util.Failure

enum HTTPMethod(val v:Byte) {
   case GET extends HTTPMethod(0)
   case POST extends HTTPMethod(1)
}

type HttpRespFunc = (Boolean,Int) => Unit;

class HttpHandle {
  var Index:Int = 0
  var respCallBack:Option[HttpRespFunc] = None;
  var respEndCallBack:Option[(Boolean,Array[Byte])  => Unit] = None;
  
  def setRequest(method:HTTPMethod,url:String):Try[Unit] = {

    val retCode = FFIRuv.httpSetRequest(RuvRuntime.runtimePtr,this.Index,method.v,url);
    if(retCode == 0) {
        Success(())
    } else {
        Failure(Exception(s"HttpHandle.setRequest err:${retCode}"))
    }
  }

  def send():Unit = {
    val retCode = FFIRuv.httpSend(RuvRuntime.runtimePtr,this.Index);
    if(retCode == 0) {
        Success(())
    } else {
        Failure(Exception(s"HttpHandle.send err:${retCode}"))
    }
  }

  def readRespBytes():Unit = {
    FFIRuv.httpReadBytes(RuvRuntime.runtimePtr,this.Index)
  }

  def setRespCallback(f:HttpRespFunc):Unit = {
    this.respCallBack = Some(f);
  }

  def setReadEndCallback(f:(Boolean,Array[Byte])  => Unit):Unit = {
    this.respEndCallBack = Some(f);
  }
}

object HttpHandle {
    def create():HttpHandle = RuvRuntime.createHttpHandle()
}