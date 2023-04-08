package transform
import core.RawComponentBuilder
import math.Vector3
import scalanative.unsafe._

/*
class Transform extends RawComponent {
    type Builder = TransformCache
};

given TransformComponent:RawComponentClass[Transform] with {
    def builder():TransformCache = new TransformCache()
}

class TransformCache extends RawComponentBuilder {
    var position:Vector3 = new Vector3(0,0,0);
    var scale:Vector3 = new Vector3(1,1,1);
    

    def build() = {
       val posPtr = stackalloc[CStruct3[Float,Float,Float]]()
       position.toPtr(posPtr)
       val scalePtr = stackalloc[CStruct3[Float,Float,Float]]()
       scale.toPtr(scalePtr)
    }
}*/