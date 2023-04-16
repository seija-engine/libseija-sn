package transform
import core.LibSeija;
import scalanative.unsafe._
import core.Entity
import core.App.worldPtr
import scalanative.unsafe.CFuncPtr1.fromScalaFunction

type SVector3 = CStruct3[CFloat,CFloat,CFloat]
type SVector4 = CStruct4[CFloat,CFloat,CFloat,CFloat]
type STransform = CStruct3[SVector4,SVector4,SVector3]
type RawTransform = STransform;

object FFISeijaTransform {
  private val addTransformModulePtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Unit]]("transform_add_module");
  private val transformAddPtr = LibSeija.getFunc[CFuncPtr3[Ptr[Byte],Long,Ptr[STransform],Unit]]("transform_add");
  private val transformDebugLogPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Long,Unit]]("transform_debug_log");
  private val transformMutViewPtr = LibSeija.getFunc[CFuncPtr3[Ptr[Byte],Long,Ptr[Byte],Unit]]("transform_mut_view");
  private val transformGetPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Long,Ptr[STransform]]]("transform_get_ptr");
  private val transformSetParentPtr = LibSeija.getFunc[CFuncPtr4[Ptr[Byte],Long,Long,Boolean,Unit]]("transform_set_parent");
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
}
