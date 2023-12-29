package com.seija.ui.core
import scalanative.unsafe._
import com.seija.math.{RawVector4, RawVector3, Vector3}
import com.seija.math.Vector4
import com.seija.core.{Entity,RawFFI,LibSeija}
import com.seija.math.Vector4RawFFI
import com.seija.ui.core.RawEventNode
import com.seija.ui.core.EventNode
import com.seija.ui.core.{RawCommonView,RawThickness,ThicknessRawFFI}
import com.seija.core.App.worldPtr
import com.seija.ui.core.CommonView
import com.seija.ui.core.Thickness
import com.seija.ui.core.RawStackLayout
import com.seija.ui.core.RawInputTextFFI
import com.seija.ui.core.RawUISize
import com.seija.ui.core.RawFlexLayout
import com.seija.ui.core.RawFlexItem
import com.seija.ui.core.RawTextFFI
import scala.scalanative.runtime.libc
import com.seija.math.Color
import com.seija.math.RawVector2
import com.seija.core.{LibSeija, App, Entity}
import scalanative.runtime._
import scala.scalanative.unsigned._
type RawSpriteSheet = Ptr[Byte]

object FFISeijaUI {
    implicit val cacheZone:Zone = Zone.open()
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
    type AddStackType = CFuncPtr6[Ptr[Byte],Long,CFloat,Byte,Ptr[RawCommonView],Ptr[com.seija.ui.core.RawUISize],Unit];
    private val entityAddStackPtr = LibSeija.getFunc[AddStackType]("entity_add_stack");
    type AddCommonViewType = CFuncPtr4[Ptr[Byte],Long,Ptr[RawCommonView],Ptr[com.seija.ui.core.RawUISize],Unit];
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

    private val entitySetLayoutWPtr = LibSeija.getFunc[CFuncPtr3[Ptr[RawCommonView],Byte,Float,Unit]]("entity_set_layout_size_w");
    private val entitySetLayoutHPtr = LibSeija.getFunc[CFuncPtr3[Ptr[RawCommonView],Byte,Float,Unit]]("entity_set_layout_size_h");
    private val uiSetPostLayoutProcessPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Ptr[Byte],Unit]]("ui_set_post_layout_process")
    private val vec_add_u64Ptr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Long,Unit]]("vec_add_u64")
    private val ui_to_ui_posPtr = LibSeija.getFunc[CFuncPtr3[Ptr[Byte],Ptr[RawVector3],Ptr[RawVector3],Unit]]("ui_to_ui_pos")

    private val entity_add_inputPtr = LibSeija.getFunc[CFuncPtr6[Ptr[Byte],Long,Long,Int,Ptr[RawVector3],CString,Unit]]("entity_add_input")
    private val entity_get_inputPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte], Long, Ptr[RawInputTextFFI]]]("entity_get_input")
    private val input_set_stringPtr = LibSeija.getFunc[CFuncPtr2[Ptr[RawInputTextFFI],CString,Unit]]("input_set_string");
    private val input_get_is_activePtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Long,Boolean]]("input_get_is_active")
    private val input_read_string_dirtyPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Long,Boolean]]("input_read_string_dirty")
    private val input_get_stringPtr = LibSeija.getFunc[CFuncPtr3[Ptr[Byte],Long,CString,Boolean]]("input_get_string")

    def addSpriteSheetModule(appPtr:Ptr[Byte]):Unit = addSpritesheetModulePtr(appPtr)
    def spriteSheetAssetGet(worldPtr:Ptr[Byte],id:Long):RawSpriteSheet = spriteSheetAssetGetPtr(worldPtr,id);
    def spritesheetGetIndex(sheet: RawSpriteSheet, name: String): Int = Zone { implicit z =>
        spritesheetGetIndexPtr(sheet, toCString(name))
    }
    def addUIModule(appPtr:Ptr[Byte]):Unit = addUIModulePtr(appPtr)
    def renderConfigSetUI(config:Ptr[Byte]):Unit = renderConfigSetUIPtr(config)

    def entityAddRect(worldPtr:Ptr[Byte],entity:Long,size:Vector4): Unit = {
       val v4Ptr:Ptr[RawVector4] = stackalloc[RawVector4]()
       v4Ptr._1 = size.x;
       v4Ptr._2 = size.y;
       v4Ptr._3 = size.z;
       v4Ptr._4 = size.w;
       entityAddRectPtr(worldPtr,entity,v4Ptr);
    }

    def entityAddUICanvas(worldPtr: Ptr[Byte], entity: Long): Unit = {
      entityAddUICanvasPtr(worldPtr, entity, 0);
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
        com.seija.ui.core.CommonViewToFFI.toRaw(view,ptrCommonView);
        val ptrUISize = stackalloc[com.seija.ui.core.RawUISize]()
        com.seija.ui.core.SizeValueToFFI.toRaw(view.uiSize,ptrUISize)
        entityAddStackPtr(worldPtr,entityId,spacing,ori,ptrCommonView,ptrUISize);
    }

    def entityAddCommonView(worldPtr:Ptr[Byte],entityId:Long,view:CommonView) = {
        val ptrCommonView = stackalloc[RawCommonView]()
        com.seija.ui.core.CommonViewToFFI.toRaw(view,ptrCommonView);
        val ptrUISize = stackalloc[com.seija.ui.core.RawUISize]()
        com.seija.ui.core.SizeValueToFFI.toRaw(view.uiSize,ptrUISize)
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
        com.seija.ui.core.CommonViewToFFI.toRaw(view,ptrCommonView);
        val ptrUISize = stackalloc[com.seija.ui.core.RawUISize]()
        com.seija.ui.core.SizeValueToFFI.toRaw(view.uiSize,ptrUISize)
        entityAddFlexPtr(worldPtr,entity,ptrCommonView,ptrUISize,flex)
    }

    def entityAddFreeLayout(worldPtr:Ptr[Byte],entity:Long,view:CommonView):Unit = {
        val ptrCommonView = stackalloc[RawCommonView]();
        com.seija.ui.core.CommonViewToFFI.toRaw(view,ptrCommonView);
        val ptrUISize = stackalloc[com.seija.ui.core.RawUISize]();
        com.seija.ui.core.SizeValueToFFI.toRaw(view.uiSize,ptrUISize)
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

    def entityTextSetString(textPtr: Ptr[RawTextFFI], text: String): Unit = Zone { implicit z =>
      entityTextSetStringPtr(textPtr, toCString(text))
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

    def SetLayoutW(view:Ptr[RawCommonView],t:Byte,value:Float):Unit = {
        entitySetLayoutWPtr(view,t,value);
    }

    def SetLayoutH(view:Ptr[RawCommonView],t:Byte,value:Float):Unit = {
        entitySetLayoutHPtr(view,t,value);
    }

    def SetOnPostLayoutProcess(appPtr: Ptr[Byte], func: Ptr[Byte]): Unit = uiSetPostLayoutProcessPtr(appPtr,func)

    def vecAddU64(ptr:Ptr[Byte],num:Long):Unit = vec_add_u64Ptr(ptr,num)

    def toUIPos(pos:Vector3):Vector3 = {
      val outPtr = stackalloc[RawVector3]()
      val curPtr = stackalloc[RawVector3]()
      pos.setToPtr(curPtr)
      ui_to_ui_posPtr(com.seija.core.App.worldPtr,curPtr,outPtr)
      Vector3(outPtr._1,outPtr._2,outPtr._3)
    }

    def entityAddInput(worldPtr: Ptr[Byte], inputEntity: Entity, textEntity: Entity, fontSize: Int, color: Color, text: String): Unit = Zone { implicit z =>
      val rawColor = stackalloc[RawVector3]()
      color.toVector3.setToPtr(rawColor)
      entity_add_inputPtr(worldPtr,inputEntity.id,textEntity.id,fontSize,rawColor,toCString(text))
    }
    def entityGetInput(worldPtr: Ptr[Byte],entity: Entity):Ptr[RawInputTextFFI] = {
         entity_get_inputPtr(worldPtr, entity.id)
    }
    def inputSetString(inputPtr:Ptr[RawInputTextFFI],string:String) = Zone { implicit z => input_set_stringPtr(inputPtr,toCString(string)) }

    def inputGetIsActive(worldPtr: Ptr[Byte],entity: Entity):Boolean = input_get_is_activePtr(worldPtr,entity.id)
    
    def inputReadStringDirty(worldPtr:Ptr[Byte],entity:Entity):Boolean = input_read_string_dirtyPtr(worldPtr,entity.id)

   
    
    val ptrStringBuffer = alloc[CChar](1024)
    def inputGetString(worldPtr:Ptr[Byte],entity:Entity):String = {
       libc.memset(toRawPtr(ptrStringBuffer),0,1024.toULong);
       if(input_get_stringPtr(worldPtr,entity.id,ptrStringBuffer)) {
         fromCString(ptrStringBuffer)
       } else {
        ""
       }
    }
}
