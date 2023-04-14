package render
import scala.scalanative.unsigned.UInt
import scala.scalanative.unsafe._
import core.RawComponentBuilder
import core.Entity
import core.RawComponent

class Camera;

class CameraBuilder  (
    var order:Int = 0,
    var layer:Int = 1,
    var cullType:Int = -1,
    var is_hdr:Boolean = false,
    var sortType:Byte = 0,
    var projection:Projection = Projection.Ortho(Orthographic()),
    var renderPath:String = "Foward"
) extends RawComponentBuilder {
    def toPtr():Ptr[RawCamera] = {
        val ptr:Ptr[RawCamera] = FFISeijaRender.renderNewCamera()
        ptr._1 = order
        ptr._2 = layer
        ptr._3 = cullType
        ptr._4 = is_hdr
        ptr._5 = sortType
        FFISeijaRender.renderCameraSetPath(ptr,renderPath)
        
        projection match {
            case Projection.Per(v) => {
                val perPtr = FFISeijaRender.renderCreatePerpectiveProjection(v)
                FFISeijaRender.renderCameraSetProjection(ptr,perPtr)
            }
            case Projection.Ortho(v) => {
                val orthoPtr = FFISeijaRender.renderCreateOrthoProjection(v)
                FFISeijaRender.renderCameraSetProjection(ptr,orthoPtr)
            }
        }
        
        ptr
    }

    def build(entity: Entity): Unit = {
        val cameraPtr = this.toPtr();
        FFISeijaRender.renderEntityAddCamera(core.App.worldPtr,entity.id,cameraPtr)
    }
}

given CameraComponent:RawComponent[Camera] with {
    type BuilderType = CameraBuilder
    type RawType = RawCamera
    def builder():BuilderType = new CameraBuilder()
    def getRaw(entity:Entity):RawCamera = FFISeijaRender.renderEntityGetCamera(core.App.worldPtr,entity.id)
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
    def toPtr(ptr:Ptr[RawOrthographic]) = {
        ptr._1 = left
        ptr._2 = right
        ptr._3 = bottom
        ptr._4 = top
        ptr._5 = near
        ptr._6 = far
    }
}

case class Perspective (
    val fov:Float = Math.toRadians(60.0).asInstanceOf[Float],
    val aspectRatio:Float = 4.0f / 3.0f,
    val near:Float = 0.01,
    val far:Float = 100,
    val dir:Byte  = 0
) {
    def toPtr(ptr:Ptr[RawPerspective]) = {
        ptr._1 = fov
        ptr._2 = aspectRatio
        ptr._3 = near
        ptr._4 = far
        ptr._5 = dir
    }
}

extension (raw:Ptr[RawCamera]) {
    def setOrder(v:Int):Unit = {
        raw._1 = v
    }
    def setLayer(v:Int):Unit = {
        raw._2 = v
    }
    def setCullType(v:Int):Unit = {
        raw._3 = v
    }
    def setIsHDR(v:Boolean):Unit = {
        raw._4 = v
    }
    def setSortType(v:Byte):Unit = {
        raw._5 = v
    }
    def setRenderPath(v:String):Unit = {
        FFISeijaRender.renderCameraSetPath(raw,v)
    }
    def setProjection(v:Projection):Unit = {
        v match {
            case Projection.Per(v) => {
                val perPtr = FFISeijaRender.renderCreatePerpectiveProjection(v)
                FFISeijaRender.renderCameraSetProjection(raw,perPtr)
            }
            case Projection.Ortho(v) => {
                val orthoPtr = FFISeijaRender.renderCreateOrthoProjection(v)
                FFISeijaRender.renderCameraSetProjection(raw,orthoPtr)
            }
        }
    }
}