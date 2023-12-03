package com.seija.ruv;
import com.seija.core.LibSeija
import scala.scalanative.unsafe.*

object FFIRuv {
    private val _ia_create_runtimePtr = LibSeija.getFunc[CFuncPtr0[Ptr[Byte]]]("_ia_create_runtime");
    private val _ia_runtime_poll_eventPtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Unit]]("_ia_runtime_poll_event");
    private val _ia_http_createPtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Int]]("_ia_http_create");
    private val _ia_http_closePtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Int,Unit]]("_ia_http_close");
    private val _ia_http_set_request = LibSeija.getFunc[CFuncPtr4[Ptr[Byte],Int,Byte,CString,Int]]("_ia_http_set_request");
    private val _ia_http_sendPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Int,Int]]("_ia_http_send");
    private val _ia_http_read_bytesPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Int,Unit]]("_ia_http_read_bytes");
    private val _ia_set_http_cb_list = LibSeija.getFunc[CFuncPtr3[Ptr[Byte],Ptr[Byte],Ptr[Byte],Unit]]("_ia_set_http_cb_list");

    def createRuntime():Ptr[Byte] = _ia_create_runtimePtr()
    def runTimePollEvent(runtime:Ptr[Byte]):Unit = _ia_runtime_poll_eventPtr(runtime);

    def createHttp(runtime:Ptr[Byte]):Int = _ia_http_createPtr(runtime)
    def closeHttp(runtime:Ptr[Byte],index:Int): Unit = _ia_http_closePtr(runtime,index);

    def httpSetRequest(runtime:Ptr[Byte],index:Int,method:Byte,url:String):Int = Zone { z =>
        _ia_http_set_request(runtime,index,method,toCString(url)(z))
    }
    def httpSend(runtime:Ptr[Byte],index:Int): CInt = _ia_http_sendPtr(runtime,index);

    def httpReadBytes(runtime:Ptr[Byte],index:Int): Unit = _ia_http_read_bytesPtr(runtime,index);

    def setEnvCallBack(runtime:Ptr[Byte],respCall:CFuncPtr,readEndCall:CFuncPtr):Unit = {
        _ia_set_http_cb_list(runtime,CFuncPtr.toPtr(respCall),CFuncPtr.toPtr(readEndCall));
    }
}