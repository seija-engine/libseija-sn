package render

import scala.scalanative.unsigned.UInt
import scala.scalanative.unsafe.Ptr

case class CameraBuilder(
    var order:Int = 0,
    var layer:Int = 1,
    var cullType:Int = -1,
    var is_hdr:Boolean = false,
    var sortType:Byte = 0,
    var projection:Projection = Projection.Ortho(Orthographic()),
    var renderPath:String = "Foward"
) {
    def toPtr():RawCamera = {
        val ptr = FFISeijaRender.renderNewCamera()
        ptr._1 = order
        ptr._2 = layer.asInstanceOf[UInt]
        ptr._3 = cullType
        ptr._4 = is_hdr
        ptr._5 = sortType
        FFISeijaRender.renderCameraSetPath(ptr,renderPath)
        ptr
    }
}


enum Projection {
    case Per(v:Perspective)
    case Ortho(v:Orthographic)
}

case class Orthographic(
    val left:Float = -1,
    val right:Float = 1,
    val bottom:Float = -1,
    val top:Float = 1,
    val near:Float = 0.001,
    val far:Float = 100
) {
    def toPtr():Ptr[Byte] = {
        
        ???
    }
}

case class Perspective (
    val fov:Float = Math.toRadians(60.0).asInstanceOf[Float],
    val aspectRatio:Float = 4.0f / 3.0f,
    val near:Float = 0.01,
    val far:Float = 100,
    val dir:Byte  = 0
) {
    def toPtr():Ptr[Byte] = {   
        ???
    }
}