package ui.binding
import ui.{Atlas,AtlasSprite}
import _root_.core.reflect.ReflectType
import _root_.core.reflect.TypeInfo

trait PropertyConverter {
  def init(args:Array[String]):Unit;
  def conv(form:Any):Any;
}

class BoolAtlasSprite extends PropertyConverter {
    var trueSprite:Option[AtlasSprite] = None;
    var falseSprite:Option[AtlasSprite] = None;
    override def init(args:Array[String]):Unit = {
        this.trueSprite = Atlas.getPath(args(0));
        this.falseSprite = Atlas.getPath(args(1));
    }
    override def conv(form:Any):Any = {
        val b = form.asInstanceOf[Boolean];
        if(b) this.trueSprite else this.falseSprite;
    }
}
import _root_.core.reflect.ReflectType;

given ReflectType[BoolAtlasSprite] with {
  override def info: TypeInfo = TypeInfo("ui.BoolAtlasSprite", ()=> BoolAtlasSprite(),None,List())
}