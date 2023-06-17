package core
import math.Vector3
import scala.scalanative.unsigned.UInt
import transform.FFISeijaTransform
import scala.scalanative.unsigned._
class Entity(val id:Long) extends AnyVal {
  def add[T](using v:RawComponent[T])(f:(builder:v.BuilderType) => Unit = null):Entity = {
     val builder = v.builder();
     if(f != null) {
       f(builder);
     }
     builder.build(this);
     this
  }
  def get[T]()(using v:RawComponent[T]):v.RawType = v.getRaw(this)

  def insertChild(child:Entity,index:Int):Unit = {
      FFISeijaTransform.transformAddChildIndex(this.id,child.id,index);
  }

  def destroy():Unit = {
    FFISeijaTransform.transformDespawn(this.id);
  }
}

object Entity {
    def from(generation:UInt,index:UInt):Entity = {
      Entity((generation.toLong << 32) | index.toLong)
    }

    def spawnEmpty():Entity = {
        Entity(FFISeijaCore.coreSpawnEntity(App.worldPtr))
    }

    

    def spawn():EntityBuilder = new EntityBuilder(Entity(FFISeijaCore.coreSpawnEntity(App.worldPtr)))
}

case class EntityBuilder(val entity:Entity) {
  
  def add[T](using v:RawComponent[T])(f:(builder:v.BuilderType) => Unit = null):EntityBuilder = {
    val builder = v.builder();
    if(f != null) {
      f(builder);
    }
    builder.build(this.entity)
    this
  }

  def build():Entity = {
    entity
  }
  
}