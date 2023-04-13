package render
import core.LibSeija;
import scalanative.unsafe._

type RawCamera = CStruct5[CInt,CUnsignedInt,CInt,CBool,CChar]

object FFISeijaRender {
    private val addRenderModulePtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Ptr[Byte],Unit]]("render_add_module");
    private val createRenderConfigPtr = LibSeija.getFunc[CFuncPtr0[Ptr[Byte]]]("render_create_config");
    private val renderConfigSetConfigPathPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],CString,Unit]]("render_config_set_config_path");
    private val renderConfigSetScriptPathPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],CString,Unit]]("render_config_set_script_path");
    private val renderConfigAddRenderLibPath = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],CString,Unit]]("render_config_add_render_lib_path");
    private val createCameraPtr = LibSeija.getFunc[CFuncPtr0[Ptr[RawCamera]]]("render_create_camera");
    private val renderCameraSetPath = LibSeija.getFunc[CFuncPtr2[Ptr[RawCamera],CString,Unit]]("render_camera_set_path");


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
}
