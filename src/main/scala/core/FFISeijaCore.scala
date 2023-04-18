package core
import scalanative.unsafe._


object FFISeijaCore {
    private val addCoreModulePtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Unit]]("core_add_module");
    private val appSetOnStartPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Ptr[Byte],Unit]]("app_set_on_start");
    private val appSetOnUpdatePtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Ptr[Byte],Unit]]("app_set_on_update");
    private val coreSpawnEntityPtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Long]]("core_spawn_entity");
    private val initLogPtr = LibSeija.getFunc[CFuncPtr1[CString,Unit]]("init_log");

    def addCoreModule(appPtr:Ptr[Byte]):Unit = {
        addCoreModulePtr(appPtr)
    }

    def appSetOnStart(appPtr:Ptr[Byte], func:Ptr[Byte]):Unit = {
        appSetOnStartPtr(appPtr,func)
    }

    def appSetOnUpdate(appPtr:Ptr[Byte], func:Ptr[Byte]):Unit = {
        appSetOnUpdatePtr(appPtr,func)
    }

    def coreSpawnEntity(worldPtr:Ptr[Byte]):Long = {
        coreSpawnEntityPtr(worldPtr)
    }

    def initLog(level:String):Unit =Zone { implicit z =>
        initLogPtr(toCString(level))
    }

    /*
    def coreSpawnEmptyEntity(worldPtr:Ptr[Byte]):FFIEntityMut = {
        val ptr = stackalloc[CStruct4[CUnsignedInt,CUnsignedInt,CSize,CSize]]();
        coreSpawnEmptyEntityPtr(worldPtr,ptr.asInstanceOf[Ptr[Byte]])
        FFIEntityMut(ptr._1,ptr._2,ptr._3,ptr._4)
    }*/
}
