package render
import core.LibSeija;
import asset.HandleUntyped;
import scalanative.unsafe._

type RawCamera = CStruct5[CInt,CInt,CInt,CBool,CChar]
type RawOrthographic = CStruct6[CFloat,CFloat,CFloat,CFloat,CFloat,CFloat]
type RawProjection = CStruct6[CFloat,CFloat,CFloat,CFloat,CFloat,CFloat]
type RawPerspective = CStruct5[CFloat,CFloat,CFloat,CFloat,Byte]

object FFISeijaRender {
    private val addRenderModulePtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Ptr[Byte],Unit]]("render_add_module");
    private val createRenderConfigPtr = LibSeija.getFunc[CFuncPtr0[Ptr[Byte]]]("render_create_config");
    private val renderConfigSetConfigPathPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],CString,Unit]]("render_config_set_config_path");
    private val renderConfigSetScriptPathPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],CString,Unit]]("render_config_set_script_path");
    private val renderConfigAddRenderLibPath = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],CString,Unit]]("render_config_add_render_lib_path");
    private val createCameraPtr = LibSeija.getFunc[CFuncPtr0[Ptr[RawCamera]]]("render_create_camera");
    private val renderCameraSetPath = LibSeija.getFunc[CFuncPtr2[Ptr[RawCamera],CString,Unit]]("render_camera_set_path");
    private val renderCreateOrthoProjectionPtr = LibSeija.getFunc[CFuncPtr1[RawOrthographic,Ptr[RawProjection]]]("render_create_ortho_projection");
    private val renderCreatePerpectiveProjectionPtr = LibSeija.getFunc[CFuncPtr1[RawPerspective,Ptr[RawProjection]]]("render_create_perpective_projection");
    private val renderCameraSetProjectionPtr = LibSeija.getFunc[CFuncPtr2[Ptr[RawCamera],Ptr[RawProjection],Unit]]("render_camera_set_projection");
    private val renderEntityAddCameraPtr = LibSeija.getFunc[CFuncPtr3[Ptr[Byte],Long,Ptr[RawCamera],Unit]]("render_entity_add_camera");
    private val renderEntityGetCameraPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Long,Ptr[RawCamera]]]("render_entity_get_camera");

    private val renderEntityAddMeshPtr = LibSeija.getFunc[CFuncPtr5[Ptr[Byte],Long,Long,Long,Long,Unit]]("render_entity_add_mesh");
    private val renderEntityAddMaterialPtr = LibSeija.getFunc[CFuncPtr5[Ptr[Byte],Long,Long,Long,Long,Unit]]("render_entity_add_material");
    private val renderConfigAddPbrPtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Unit]]("render_config_add_pbr_plugin");

    def createRenderConfig(): Ptr[Byte] = createRenderConfigPtr()

    def addRenderModule(config: Ptr[Byte], appPtr: Ptr[Byte]): Unit = addRenderModulePtr(appPtr,config )

    def renderConfigSetConfigPath(config: Ptr[Byte], path: String): Unit = Zone { implicit z =>
        renderConfigSetConfigPathPtr(config, toCString(path))
    }

    def renderConfigSetScriptPath(config: Ptr[Byte], path: String): Unit = Zone { implicit z =>
        renderConfigSetScriptPathPtr(config, toCString(path))
    }

    def renderConfigAddRenderLibPath(config: Ptr[Byte], path: String): Unit = Zone { implicit z =>
        renderConfigAddRenderLibPath(config, toCString(path))
    }

    def renderNewCamera(): Ptr[RawCamera] = createCameraPtr()

    def renderCameraSetPath(camera: Ptr[RawCamera], path: String): Unit = Zone { implicit z =>
        renderCameraSetPath(camera, toCString(path))
    }

    def renderCreateOrthoProjection(ortho: Orthographic): Ptr[RawProjection] = {
        val rawOrtho = stackalloc[RawOrthographic]()
        ortho.toPtr(rawOrtho)
        renderCreateOrthoProjectionPtr(rawOrtho)
    }

    def renderCreatePerpectiveProjection(per: Perspective): Ptr[RawProjection] = {
        val rawPer = stackalloc[RawPerspective]()
        per.toPtr(rawPer)
        renderCreatePerpectiveProjectionPtr(rawPer)
    }

    def renderCameraSetProjection(camera: Ptr[RawCamera], projection: Ptr[RawProjection]): Unit = {
        renderCameraSetProjectionPtr(camera, projection)
    }

    def renderEntityAddCamera(worldPtr: Ptr[Byte], entityID: Long, camera: Ptr[RawCamera]): Unit = {
        renderEntityAddCameraPtr(worldPtr, entityID, camera)
    }

    def renderEntityGetCamera(worldPtr: Ptr[Byte], entityID: Long): Ptr[RawCamera] = {
        renderEntityGetCameraPtr(worldPtr, entityID)
    }


    def renderEntityAddMesh(worldPtr: Ptr[Byte], entityID: Long,handle:HandleUntyped): Unit = {
        renderEntityAddMeshPtr(worldPtr, entityID, handle.id, handle.ta, handle.tb)
    }

    def renderEntityAddMaterial(worldPtr: Ptr[Byte], entityID: Long,handle:HandleUntyped): Unit = {
        renderEntityAddMaterialPtr(worldPtr, entityID, handle.id, handle.ta, handle.tb)
    }

    def renderConfigAddPbr(config: Ptr[Byte]): Unit = {
        renderConfigAddPbrPtr(config)
    }
}
