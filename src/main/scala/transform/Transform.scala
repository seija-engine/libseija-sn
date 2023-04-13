package transform
import math.{Vector3,Quat}
import scalanative.unsafe._
import core.{RawComponentBuilder,RawComponent};
import core.Entity

class Transform;

class TransformBuilder extends RawComponentBuilder {
    var position:Vector3 = new Vector3(0,0,0)
    var scale:Vector3 = new Vector3(1,1,1)
    var quat:Quat = new Quat(0,0,0,1)
    def build(entity:Entity):Unit = {
        FFISeijaTransform.transformAdd(core.App.worldPtr,entity,this)
    }
}   

given TransformComponent:RawComponent[Transform] with {
    type BuilderType = TransformBuilder
    type RawType = RawTransform
    def builder():BuilderType = new TransformBuilder()

    def getRaw(entity:Entity):RawTransform = FFISeijaTransform.transformGet(core.App.worldPtr,entity.id)
}

extension (t:RawTransform) {
    def setPosition(v3:Vector3):Unit = {
        v3.toPtr4(t.at1)
    }
}
    