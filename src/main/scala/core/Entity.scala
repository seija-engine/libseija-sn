package core
import math.Vector3

class Entity(private val id:Long) extends AnyVal {
  
}

object Entity {

    def spawnEmpty() :Entity = {
      val id = FFISeijaCore.coreSpawnEntity(App.worldPtr);
      new Entity(id);
    }

    def spawn():EntityBuilder = new EntityBuilder()
}

case class EntityBuilder() {
  
  def add[T](using v:RawComponent[T])(f:(builder:v.BuilderType) => Unit = null):EntityBuilder = {
    if(f != null) {
      val builder = v.builder();
      f(builder);  
    }
    this
  }
  
}