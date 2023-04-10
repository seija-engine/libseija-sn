package core;
trait RawComponent[T] {
    type BuilderType <: RawComponentBuilder;
    def builder():BuilderType;
}

trait RawComponentBuilder {
  def build(entity:Entity):Unit;
}