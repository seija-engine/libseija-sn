package ui.core
import scalanative.unsafe._
import math.RawVector4
import math.Vector4
import _root_.core.{Entity,RawFFI,LibSeija}
import math.Vector4RawFFI
import ui.core.RawEventNode
import ui.core.EventNode
import ui.core.{RawCommonView,RawThickness,ThicknessRawFFI}
import _root_.core.App.worldPtr
import ui.core.CommonView
import ui.core.Thickness
import ui.core.RawStackLayout
import ui.core.RawUISize
import ui.core.RawFlexLayout
import ui.core.RawFlexItem
import ui.core.RawTextFFI
import scala.scalanative.runtime.libc
import math.Color
import math.RawVector2


type RawSpriteSheet = Ptr[Byte]

object FFISeijaUI {
    private val addSpritesheetModulePtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Unit]]("spritesheet_add_module");
    private val spriteSheetAssetGetPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Long,RawSpriteSheet]]("spritesheet_asset_get");
    private val spritesheetGetIndexPtr = LibSeija.getFunc[CFuncPtr2[RawSpriteSheet,CString,Int]]("spritesheet_get_index");
    private val addUIModulePtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Unit]]("ui_add_module");
    private val renderConfigSetUIPtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Unit]]("render_config_set_ui")
    private val entityAddRectPtr = LibSeija.getFunc[CFuncPtr3[Ptr[Byte],Long,Ptr[RawVector4],Unit]]("entity_add_rect2d");
    private val entityAddUICanvasPtr = LibSeija.getFunc[CFuncPtr3[Ptr[Byte],Long,Byte,Unit]]("entity_add_ui_canvas");
    private val entityAddCanvasPtr = LibSeija.getFunc[CFuncPtr3[Ptr[Byte],Long,Boolean,Unit]]("entity_add_canvas");
    private val entityAddUISystemPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Long,Unit]]("entity_add_ui_system");
    private val entityAddSpriteSimplePtr = LibSeija.getFunc[CFuncPtr5[Ptr[Byte],Long,Int,Long,Ptr[RawVector4],Unit]]("entity_add_sprite_simple");
    private val entityAddSpriteSlicePtr = LibSeija.getFunc[CFuncPtr6[Ptr[Byte],Long,Int,Long,Ptr[RawVector4],Ptr[RawVector4],Unit]]("entity_add_sprite_slice");
    private val entityGetSpritePtr = LibSeija.getFunc[CFuncPtr3[Ptr[Byte],Long,Boolean,Ptr[Byte]]]("entity_get_sprite");
    private val spriteSetSpritePtr = LibSeija.getFunc[CFuncPtr4[Ptr[Byte],Ptr[Byte],Int,Long,Unit]]("sprite_set_sprite");
    private val spriteSetColorPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Ptr[RawVector4],Unit]]("sprite_set_color");
    private val entityAddEventNodePtr = LibSeija.getFunc[CFuncPtr3[Ptr[Byte],Long,Ptr[RawEventNode],Unit]]("entity_add_event_node");
    private val entityRemoveEventPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Long,Boolean]]("entity_remove_event_node")
    private val readUIEventsPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Ptr[Byte],Unit]]("read_ui_events");
    type AddStackType = CFuncPtr6[Ptr[Byte],Long,CFloat,Byte,Ptr[RawCommonView],Ptr[ui.core.RawUISize],Unit];
    private val entityAddStackPtr = LibSeija.getFunc[AddStackType]("entity_add_stack");
    type AddCommonViewType = CFuncPtr4[Ptr[Byte],Long,Ptr[RawCommonView],Ptr[ui.core.RawUISize],Unit];
    private val entityAddCommonviewPtr = LibSeija.getFunc[AddCommonViewType]("entity_add_commonview");
    private val entityGetStackPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Long,Ptr[CStruct2[CFloat,Byte]]]]("entity_get_stack");
    private val entityGetCommonViewPtr =LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Long,Ptr[RawCommonView]]]("entity_get_commonview");
    type AddFlexType = CFuncPtr5[Ptr[Byte],Long,Ptr[RawCommonView],Ptr[RawUISize],Ptr[RawFlexLayout],Unit];
    private val entityAddFlexPtr = LibSeija.getFunc[AddFlexType]("entity_add_flex");
    private val entityAddFlexItemPtr = LibSeija.getFunc[CFuncPtr3[Ptr[Byte],Long,Ptr[RawFlexItem],Unit]]("entity_add_flexitem");
    private val entityAddTextPtr = LibSeija.getFunc[CFuncPtr6[Ptr[Byte],Long,Ptr[RawTextFFI],Int,CString,Long,Unit]]("entity_add_text");
    private val spritesheetBeginReadPtr = LibSeija.getFunc[CFuncPtr3[RawSpriteSheet,Ptr[Int],Ptr[Int],Ptr[Byte]]]("spritesheet_begin_read");
    private val spritesheetGetInfoPtr = LibSeija.getFunc[CFuncPtr4[Ptr[Byte],Int,Ptr[Int],CString,Unit]]("spritesheet_get_info");
    private val spritesheetEndReadPtr = LibSeija.getFunc[CFuncPtr1[RawSpriteSheet,Unit]]("spritesheet_end_read");
    private val entityGetTextPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Long,Ptr[RawTextFFI]]]("entity_get_text");
    private val entityTextSetStringPtr = LibSeija.getFunc[CFuncPtr2[Ptr[RawTextFFI],CString,Unit]]("entity_text_setstring");
    type AddFreeType = CFuncPtr4[Ptr[Byte],Long,Ptr[RawCommonView],Ptr[RawUISize],Unit];
    private val entityAddFreeLayoutPtr = LibSeija.getFunc[AddFreeType]("entity_add_free_layout");
    private val entityAddFreeItemPtr = LibSeija.getFunc[CFuncPtr4[Ptr[Byte],Long,Float,Float,Unit]]("entity_add_layout_freeitem");
    private val entityGetFreeItemPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Long,Ptr[RawVector2]]]("entity_get_layout_freeitem");
    private val entityGetRect2dPtr = LibSeija.getFunc[CFuncPtr3[Ptr[Byte],Long,Boolean,Ptr[RawVector4]]]("entity_get_rect2d");

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

    def entityAddCanvas(worldPtr:Ptr[Byte],entity:Long,isClip:Boolean) = {
        entityAddCanvasPtr(worldPtr,entity,isClip);
    }

    def entityAddUISystem(worldPtr:Ptr[Byte],entity:Long) = {
        entityAddUISystemPtr(worldPtr,entity)
    }

    def entityAddSpriteSimple(worldPtr:Ptr[Byte],entity:Long,index:Int,atlasId:Long,color:Vector4) = {
        val v4Ptr = stackalloc[RawVector4]()
        Vector4RawFFI.toRaw(color,v4Ptr);
        entityAddSpriteSimplePtr(worldPtr,entity,index,atlasId,v4Ptr)
    }

    def entityAddSpriteSlice(worldPtr:Ptr[Byte],entity:Long,index:Int,atlasId:Long,thickness:Thickness,color:Vector4) = {
        val thicknessPtr = stackalloc[RawThickness]()
        ThicknessRawFFI.toRaw(thickness,thicknessPtr);
        val colorPtr = stackalloc[RawVector4]()
        Vector4RawFFI.toRaw(color,colorPtr);
        entityAddSpriteSlicePtr(worldPtr,entity,index,atlasId,thicknessPtr,colorPtr)
    }

    def entityGetSprite(worldPtr:Ptr[Byte],entity:Long,isMut:Boolean):Ptr[Byte] = entityGetSpritePtr(worldPtr,entity,isMut)

    def entityAddEventNode(worldPtr:Ptr[Byte],entity:Long,eventNodePtr:Ptr[RawEventNode]) =  Zone { implicit z =>
        entityAddEventNodePtr(worldPtr,entity,eventNodePtr)
       
    }

    def entityRemoveEventNode(worldPtr:Ptr[Byte],entity:Long):Boolean = entityRemoveEventPtr(worldPtr,entity)

    def readUIEvents(worldPtr:Ptr[Byte],fPtr:CFuncPtr) = {
        val funcPtr = CFuncPtr.toPtr(fPtr);
        readUIEventsPtr(worldPtr,funcPtr);
    }

    def entityAddStack(worldPtr:Ptr[Byte],entityId:Long,spacing:Float,ori:Byte,view:CommonView) = {
        val ptrCommonView = stackalloc[RawCommonView]()
        ui.core.CommonViewToFFI.toRaw(view,ptrCommonView);
        val ptrUISize = stackalloc[ui.core.RawUISize]()
        ui.core.SizeValueToFFI.toRaw(view.uiSize,ptrUISize)
        entityAddStackPtr(worldPtr,entityId,spacing,ori,ptrCommonView,ptrUISize);
    }

    def entityAddCommonView(worldPtr:Ptr[Byte],entityId:Long,view:CommonView) = {
        val ptrCommonView = stackalloc[RawCommonView]()
        ui.core.CommonViewToFFI.toRaw(view,ptrCommonView);
        val ptrUISize = stackalloc[ui.core.RawUISize]()
        ui.core.SizeValueToFFI.toRaw(view.uiSize,ptrUISize)
        entityAddCommonviewPtr(worldPtr,entityId,ptrCommonView,ptrUISize);
    }

    def entityGetStackView(worldPtr:Ptr[Byte],entityId:Long):Ptr[CStruct2[CFloat,Byte]] = {
        entityGetStackPtr(worldPtr,entityId)
    }

    def entityGetCommonView(worldPtr:Ptr[Byte],entityId:Long):Ptr[RawCommonView] = {
        entityGetCommonViewPtr(worldPtr,entityId)
    }

    def spriteSetSptite(worldPtr:Ptr[Byte],spritePtr:Ptr[Byte],index:Int,atlasId:Long) = {
        spriteSetSpritePtr(worldPtr,spritePtr,index,atlasId)
    }

    def spriteSetColor(spritePtr:Ptr[Byte],color:Color):Unit = {
        val ptrVec4 = stackalloc[RawVector4]();
        Vector4RawFFI.toRaw(color.toVector4(),ptrVec4);
        spriteSetColorPtr(spritePtr,ptrVec4);
    }

    def entityAddFlex(worldPtr:Ptr[Byte],entity:Long,view:CommonView,flex:Ptr[RawFlexLayout]) = {
        val ptrCommonView = stackalloc[RawCommonView]()
        ui.core.CommonViewToFFI.toRaw(view,ptrCommonView);
        val ptrUISize = stackalloc[ui.core.RawUISize]()
        ui.core.SizeValueToFFI.toRaw(view.uiSize,ptrUISize)
        entityAddFlexPtr(worldPtr,entity,ptrCommonView,ptrUISize,flex)
    }

    def entityAddFreeLayout(worldPtr:Ptr[Byte],entity:Long,view:CommonView):Unit = {
        val ptrCommonView = stackalloc[RawCommonView]();
        ui.core.CommonViewToFFI.toRaw(view,ptrCommonView);
        val ptrUISize = stackalloc[ui.core.RawUISize]();
        ui.core.SizeValueToFFI.toRaw(view.uiSize,ptrUISize)
        entityAddFreeLayoutPtr(worldPtr,entity,ptrCommonView,ptrUISize);
    }

    def entityAddFlexItem(worldPtr:Ptr[Byte],entity:Long,flexItemPtr:Ptr[RawFlexItem]) = {
        entityAddFlexItemPtr(worldPtr,entity,flexItemPtr)
    }

    def entityAddText(worldPtr:Ptr[Byte],entity:Long,ptrText:Ptr[RawTextFFI],size:Int,text:String,fontId:Long) = Zone { implicit z =>
        entityAddTextPtr(worldPtr,entity,ptrText,size,toCString(text),fontId)
    }

    def spriteBeginRead(ptr:RawSpriteSheet):(Ptr[Byte],Int,Int) = {
        val ptrCount = stackalloc[Int]()
        val ptrMaxCharCount = stackalloc[Int]()
        val ptrFFI = spritesheetBeginReadPtr(ptr,ptrCount,ptrMaxCharCount);     
        (ptrFFI,!ptrCount,!ptrMaxCharCount)
    }

    def spriteSheetGetInfo(ptr:Ptr[Byte],index:Int,outNamePtr:CString):(Int,String) = {
       val outIndex:Ptr[Int] = stackalloc[Int]()
       spritesheetGetInfoPtr(ptr,index,outIndex,outNamePtr)
      
       val outName = fromCString(outNamePtr);
       (!outIndex,outName)
    }

    def spriteEndRead(ptr:RawSpriteSheet) = spritesheetEndReadPtr(ptr)

    def entityGetText(worldPtr:Ptr[Byte],entity:Long):Ptr[RawTextFFI] = entityGetTextPtr(worldPtr,entity)

    def entityTextSetString(textPtr:Ptr[RawTextFFI],text:String) = Zone { implicit z =>
        entityTextSetStringPtr(textPtr,toCString(text))
    }

    def entityAddFreeItem(worldPtr:Ptr[Byte],entity:Long,x:Float,y:Float):Unit = {
        entityAddFreeItemPtr(worldPtr,entity,x,y)
    }

    def entityGetFreeItem(worldPtr:Ptr[Byte],entity:Long):Ptr[RawVector2] = {
        entityGetFreeItemPtr(worldPtr,entity)
    }

    def entityGetRect2d(worldPtr:Ptr[Byte],entity:Long,isMut:Boolean):Ptr[RawVector4] = {
        entityGetRect2dPtr(worldPtr,entity,isMut)
    }
}
