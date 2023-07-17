package transform
import core.LibSeija;
import scalanative.unsafe._
import core.Entity
import core.App.worldPtr
import scalanative.unsafe.CFuncPtr1.fromScalaFunction
import scala.scalanative.unsigned._



object FFISeijaTransform {
  private val addTransformModulePtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Unit]]("transform_add_module");
  private val transformAddPtr = LibSeija.getFunc[CFuncPtr3[Ptr[Byte],Long,Ptr[STransform],Unit]]("transform_add");
  private val transformDebugLogPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Long,Unit]]("transform_debug_log");
  private val transformMutViewPtr = LibSeija.getFunc[CFuncPtr3[Ptr[Byte],Long,Ptr[Byte],Unit]]("transform_mut_view");
  private val transformGetPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Long,Ptr[STransform]]]("transform_get_ptr");
  private val transformSetParentPtr = LibSeija.getFunc[CFuncPtr4[Ptr[Byte],Long,Long,Boolean,Unit]]("transform_set_parent");
  private val transformAddChildIndexPtr = LibSeija.getFunc[CFuncPtr4[Ptr[Byte],Long,Long,Int,Unit]]("transform_add_child_index");
  private val transformDespawnPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Long,Unit]]("transform_despawn");
  private val transform_set_activePtr = LibSeija.getFunc[CFuncPtr3[Ptr[Byte],Long,Boolean,Unit]]("transform_set_active");
  private val transform_is_active_globalPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Long,Boolean]]("transform_is_active_global")
  private val transform_add_state_infoPtr = LibSeija.getFunc[CFuncPtr3[Ptr[Byte],Long,Boolean,Unit]]("transform_add_state_info")

  def addTransformModule(appPtr:Ptr[Byte]):Unit = {
      addTransformModulePtr(appPtr)
  }

  def transformAdd(worldPtr:Ptr[Byte],entity:Entity,transformBuilder:TransformBuilder):Unit = {
      
      val trans = stackalloc[STransform]();
       transformBuilder.scale.toPtr4(trans.at1);
      transformBuilder.quat.toPtr(trans.at2);
      transformBuilder.position.toPtr(trans.at3);
      transformAddPtr(worldPtr,entity.id,trans)
     
  }

  def transformDebugLog(worldPtr:Ptr[Byte],entity:Entity):Unit = transformDebugLogPtr(worldPtr,entity.id)

  def transformMutView(worldPtr:Ptr[Byte],entity:Entity,fPtr:CFuncPtr):Unit = {
    val funcPtr = CFuncPtr.toPtr(fPtr);
    transformMutViewPtr(worldPtr,entity.id,funcPtr);
  }

  def transformGet(worldPtr:Ptr[Byte],id:Long):Ptr[STransform] = transformGetPtr(worldPtr,id)

  def transformSetParent(worldPtr:Ptr[Byte],entity:Long,parent:Long,isNull:Boolean) = {
      transformSetParentPtr(core.App.worldPtr,entity,parent,isNull)
  }

  def transformAddChildIndex(curEntity:Long,childEntity:Long,index:Int):Unit = {
      transformAddChildIndexPtr(core.App.worldPtr,curEntity,childEntity,index);
  }

  def transformDespawn(curEntity:Long):Unit = {
      transformDespawnPtr(core.App.worldPtr,curEntity);
  }

  def setActive(entity:Entity,active:Boolean):Unit = transform_set_activePtr(core.App.worldPtr,entity.id,active)

  def isActiveGlobal(entity:Entity):Boolean = transform_is_active_globalPtr(core.App.worldPtr,entity.id)

  def addStateInfo(entity:Entity,active:Boolean):Unit = {
    transform_add_state_infoPtr(core.App.worldPtr,entity.id,active)
  }
}
