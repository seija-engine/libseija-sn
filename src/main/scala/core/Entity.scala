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

class TestData;

abstract class  RawComponentBuilder {
  def build():Unit
}

trait RawComponent[T] {
  type Cache <: RawComponentBuilder
}

class TestDataCache extends RawComponentBuilder {
    var pos = new Vector3(0,0,0);
    var scale = new Vector3(1,1,1);
    def build() = {
      println("build");
    }
}

given TestDataComponent:RawComponent[TestData] with {
  type Cache = TestDataCache
}

case class EntityBuilder() {
  def add[T](using v:RawComponent[T])(cache:(v.Cache) => Unit ):EntityBuilder = {
    
    this
  }

  def build():Entity = {
    Entity.spawnEmpty()
  }

}