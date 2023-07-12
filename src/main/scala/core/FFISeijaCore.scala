package core
import scalanative.unsafe._
import scalanative.unsigned.ULong
import scala.scalanative.unsigned.UInt
type RawTime = CStruct2[Float,ULong]

object FFISeijaCore {
    private val addCoreModulePtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Unit]]("core_add_module")
    private val appSetOnStartPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Ptr[Byte],Unit]]("app_set_on_start")
    private val appSetOnUpdatePtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Ptr[Byte],Unit]]("app_set_on_update")
    private val coreSpawnEntityPtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Long]]("core_spawn_entity")
    private val initLogPtr = LibSeija.getFunc[CFuncPtr1[CString,Unit]]("init_log")
    private val coreWorldGetTimePtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Ptr[RawTime]]]("core_world_get_time")
    private val isFrameDirtyPtr = LibSeija.getFunc[CFuncPtr4[Ptr[Byte],Long,ULong,Int,Boolean]]("is_frame_dirty")


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

    def coreWorldGetTime(worldPtr:Ptr[Byte]):Ptr[RawTime] = coreWorldGetTimePtr(worldPtr)

    def isFrameDirty(entity: Entity,checkFrame:ULong,index:Int):Boolean = {
     isFrameDirtyPtr(core.App.worldPtr,entity.id,checkFrame,index)
    }
   
}
