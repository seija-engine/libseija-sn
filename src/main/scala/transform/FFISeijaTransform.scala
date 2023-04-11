package transform
import core.LibSeija;
import scalanative.unsafe._
import core.Entity

type SVector3 = CStruct3[CFloat,CFloat,CFloat]
type SVector4 = CStruct4[CFloat,CFloat,CFloat,CFloat]
type STransform = CStruct3[SVector4,SVector4,SVector3]

object FFISeijaTransform {
  private val addTransformModulePtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Unit]]("transform_add_module");
  private val transformAddPtr = LibSeija.getFunc[CFuncPtr3[Ptr[Byte],Long,Ptr[STransform],Unit]]("transform_add");
  private val transformDebugLogPtr = LibSeija.getFunc[CFuncPtr2[Ptr[Byte],Long,Unit]]("transform_debug_log");

  def addTransformModule(appPtr:Ptr[Byte]):Unit = {
      addTransformModulePtr(appPtr)
  }

  def transformAdd(worldPtr:Ptr[Byte],entity:Entity,transformBuilder:TransformBuilder):Unit = {
      println("transformAdd");
      val trans = stackalloc[STransform]();
      transformBuilder.position.toPtr4(trans.at1);
      transformBuilder.quat.toPtr(trans.at2);
      transformBuilder.scale.toPtr(trans.at3);
      transformAddPtr(worldPtr,entity.id,trans)
      println("transformAdd");
  }

  def transformDebugLog(worldPtr:Ptr[Byte],entity:Entity):Unit = {
      transformDebugLogPtr(worldPtr,entity.id)
  }
}
