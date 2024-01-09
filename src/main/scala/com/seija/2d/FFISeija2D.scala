package com.seija.`2d`
import scalanative.unsafe._
import scala.util.boundary
import com.seija.math.Vector4RawFFI
import com.seija.core.LibSeija
import com.seija.math.RawVector3
import com.seija.core.App.worldPtr
import com.seija.core.Entity
import com.seija.math.Vector3
import com.seija.math.RawVector4
import com.seija.math.Vector4


object FFISeija2D {
    private val r2d_add_modulePtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Unit]]("r2d_add_module")
    def r2dAddModule(appPtr:Ptr[Byte]):Unit = r2d_add_modulePtr(appPtr)

    private val entity_add_screen_scalerPtr = LibSeija.getFunc[CFuncPtr5[Ptr[Byte],Long,Long,Int,Ptr[RawVector3],Unit]]("entity_add_screen_scaler")
    def addScreenScaler(worldPtr:Ptr[Byte],entity:Entity,camera:Entity,mode:Int,exParams:Vector3):Unit = {
        val vec3Ptr = stackalloc[RawVector3]()
        exParams.setToPtr(vec3Ptr)
        entity_add_screen_scalerPtr(worldPtr,entity.id,camera.id,mode,vec3Ptr);
    }

    private val entity_add_sprite_2dPtr = LibSeija.getFunc[CFuncPtr5[Ptr[Byte],Long,Long,Int,Ptr[RawVector4],Unit]]("entity_add_sprite_2d");
    def addSprite2D(worldPtr:Ptr[Byte],atlasId:Long,entity:Entity,index:Int,color:Vector4):Unit = {
        val vec4Ptr = stackalloc[RawVector4]()
        Vector4RawFFI.toRaw(color,vec4Ptr);
       
        entity_add_sprite_2dPtr(worldPtr,atlasId,entity.id,index,vec4Ptr)
    }
}
