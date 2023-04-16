package core;

trait RawComponent[T] {
    type BuilderType <: RawComponentBuilder;
    type RawType;
    def builder():BuilderType;
    def getRaw(entity:Entity):RawType;
}

trait RawComponentBuilder {
  def build(entity:Entity):Unit;
}