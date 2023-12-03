package com.seija.ruv
import scala.scalanative.unsafe._
import scala.scalanative.unsafe
import java.nio.charset.{Charset, StandardCharsets}
import scalanative.unsigned._
import java.nio.ByteBuffer
import com.seija.ruv.http.HttpHandle;
import scala.collection.mutable.ArrayBuffer

object RuvRuntime {
  val runtimePtr: Ptr[Byte] = FFIRuv.createRuntime()
  FFIRuv.setEnvCallBack(runtimePtr, 
                        CFuncPtr3.fromScalaFunction(_on_http_resp),
                        CFuncPtr3.fromScalaFunction(_on_http_resp_read_end));

  var httpHandleList:ArrayBuffer[HttpHandle] = ArrayBuffer.empty

  def createHttpHandle():HttpHandle = {
    FFIRuv.createHttp(runtimePtr);
    val handle = HttpHandle();
    this.httpHandleList.addOne(handle);
    handle.Index = this.httpHandleList.length - 1;
    handle
  }

  def update():Unit = {
    FFIRuv.runTimePollEvent(runtimePtr);
  }

  def _on_http_resp(index:Int,isError:Boolean,statusCode:Int):Unit = {
    val handle = this.httpHandleList(index);
    handle.respCallBack.foreach {call => 
      call(isError,statusCode);  
    }
  }

  def _on_http_resp_read_end(index:Int,size:Long,ptr:Ptr[Byte]):Unit = {
    val bytes = new Array[Byte](size.toInt)
    scalanative.runtime.libc.memcpy(bytes.at(0), ptr, size.toULong)
    val handle = this.httpHandleList(index);
    handle.respEndCallBack.foreach {call => 
      call(size == 0,bytes);  
    }
  }
}