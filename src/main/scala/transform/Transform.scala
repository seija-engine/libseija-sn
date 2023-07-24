package transform
import math.{Vector3,Quat}
import scalanative.unsafe._
import core.{RawComponentBuilder,RawComponent};
import core.Entity

type SVector3 = CStruct3[CFloat,CFloat,CFloat]
type SVector4 = CStruct4[CFloat,CFloat,CFloat,CFloat]
type STransform = CStruct3[SVector4,SVector4,SVector3]
type RawTransform = CStruct6[SVector4,SVector4,SVector4,SVector4,SVector4,SVector4]

class Transform

case class TransformMatrix(pos:Vector3,scale:Vector3,r:Quat)

class TransformBuilder extends RawComponentBuilder {
    var position:Vector3 = new Vector3(0,0,0)
    var scale:Vector3 = new Vector3(1,1,1)
    var quat:Quat = new Quat(0,0,0,1)
    var parent:Option[Entity] = None;
    def build(entity:Entity):Unit = {
        FFISeijaTransform.transformAdd(core.App.worldPtr,entity,this)
        if(parent.isDefined) {
            FFISeijaTransform.transformSetParent(core.App.worldPtr,entity.id,parent.get.id,false)
        }
    }
}   

object Transform {
  given TransformComponent:RawComponent[Transform] with {
    type BuilderType = TransformBuilder
    type RawType = RawTransform
    def builder():BuilderType = new TransformBuilder()

    def getRaw(entity:Entity,isMut:Boolean):RawTransform = FFISeijaTransform.transformGet(core.App.worldPtr,entity.id)
  }

  def relativeTo(child:Entity,parent:Option[Entity]):TransformMatrix =FFISeijaTransform.calcRelative(child, parent)
}



extension (t:RawTransform) {
    def getLocalPosition:Vector3 = {
        Vector3(t._3._1,t._3._2,t._3._3)
    }

    def getWorldPosition:Vector3 = {
      Vector3(t._6._1,t._6._2,t._6._3)
    }

}
    