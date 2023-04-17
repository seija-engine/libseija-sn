package ui
import scalanative.unsafe._
import math.RawVector4
import math.Vector4
import _root_.core.{Entity,RawFFI,LibSeija}
import math.Vector4RawFFI
import ui.core.RawEventNode
import ui.core.EventNode


type RawSpriteSheet = Ptr[Byte]

object FFISeijaUI {
    private val addSpritesheetModulePtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Unit]]("spritesheet_add_module");
    private val spriteSheetAssetGetPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Long,RawSpriteSheet]]("spritesheet_asset_get");
    private val spritesheetGetIndexPtr = LibSeija.getFunc[CFuncPtr2[RawSpriteSheet,CString,Int]]("spritesheet_get_index");
    private val addUIModulePtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Unit]]("ui_add_module");
    private val renderConfigSetUIPtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Unit]]("render_config_set_ui")
    private val entityAddRectPtr = LibSeija.getFunc[CFuncPtr3[Ptr[Byte],Long,Ptr[RawVector4],Unit]]("entity_add_rect2d");
    private val entityAddUICanvasPtr = LibSeija.getFunc[CFuncPtr3[Ptr[Byte],Long,Byte,Unit]]("entity_add_ui_canvas");
    private val entityAddCanvasPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Long,Unit]]("entity_add_canvas");
    private val entityAddUISystemPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Long,Unit]]("entity_add_ui_system");
    private val entityAddSpriteSimplePtr = LibSeija.getFunc[CFuncPtr5[Ptr[Byte],Long,Int,Long,Ptr[RawVector4],Unit]]("entity_add_sprite_simple");
    private val entityAddSpriteSlicePtr = LibSeija.getFunc[CFuncPtr6[Ptr[Byte],Long,Int,Long,Ptr[RawVector4],Ptr[RawVector4],Unit]]("entity_add_sprite_slice");
    private val entityAddEventNodePtr = LibSeija.getFunc[CFuncPtr4[Ptr[Byte],Long,Ptr[RawEventNode],CString,Unit]]("entity_add_event_node");
    private val readUIEventsPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Ptr[Byte],Unit]]("read_ui_events");

    def addSpriteSheetModule(appPtr:Ptr[Byte]):Unit = addSpritesheetModulePtr(appPtr)
    def spriteSheetAssetGet(worldPtr:Ptr[Byte],id:Long):RawSpriteSheet = spriteSheetAssetGetPtr(worldPtr,id);
    def spritesheetGetIndex(sheet: RawSpriteSheet, name: String): Int = Zone { implicit z =>
        spritesheetGetIndexPtr(sheet, toCString(name))
    }
    def addUIModule(appPtr:Ptr[Byte]):Unit = addUIModulePtr(appPtr)
    def renderConfigSetUI(config:Ptr[Byte]):Unit = renderConfigSetUIPtr(config)

    def entityAddRect(worldPtr:Ptr[Byte],entity:Long,size:Vector4) = {
       val v4Ptr:Ptr[RawVector4] = stackalloc[RawVector4]()
       v4Ptr._1 = size.x;
       v4Ptr._2 = size.y;
       v4Ptr._3 = size.z;
       v4Ptr._4 = size.w;
       entityAddRectPtr(worldPtr,entity,v4Ptr);
    }

    def entityAddUICanvas(worldPtr:Ptr[Byte],entity:Long) = {
        entityAddUICanvasPtr(worldPtr,entity,0);
    }

    def entityAddCanvas(worldPtr:Ptr[Byte],entity:Long) = {
        entityAddCanvasPtr(worldPtr,entity);
    }

    def entityAddUISystem(worldPtr:Ptr[Byte],entity:Long) = {
        entityAddUISystemPtr(worldPtr,entity)
    }

    def entityAddSpriteSimple(worldPtr:Ptr[Byte],entity:Long,index:Int,atlasId:Long,color:Vector4) = {
        val v4Ptr = stackalloc[RawVector4]()
        Vector4RawFFI.toRaw(color,v4Ptr);
        entityAddSpriteSimplePtr(worldPtr,entity,index,atlasId,v4Ptr)
    }

    def entityAddSpriteSlice(worldPtr:Ptr[Byte],entity:Long,index:Int,atlasId:Long,thickness:Vector4,color:Vector4) = {
        val thicknessPtr = stackalloc[RawVector4]()
        Vector4RawFFI.toRaw(thickness,thicknessPtr);
        val colorPtr = stackalloc[RawVector4]()
        Vector4RawFFI.toRaw(color,colorPtr);
        entityAddSpriteSlicePtr(worldPtr,entity,index,atlasId,thicknessPtr,colorPtr)
    }

    def entityAddEventNode(worldPtr:Ptr[Byte],entity:Long,eventNodePtr:Ptr[RawEventNode],name:String) =  Zone { implicit z =>
        entityAddEventNodePtr(worldPtr,entity,eventNodePtr,toCString(name))
    }

    def readUIEvents(worldPtr:Ptr[Byte],fPtr:CFuncPtr) = {
        val funcPtr = CFuncPtr.toPtr(fPtr);
        readUIEventsPtr(worldPtr,funcPtr);
    }
}
