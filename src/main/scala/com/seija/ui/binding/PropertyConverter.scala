package com.seija.ui.binding
import com.seija.ui.{Atlas,AtlasSprite}
import com.seija.core.reflect.ReflectType
import com.seija.core.reflect.TypeInfo
import com.seija.core.reflect.{ReflectType, TypeInfo}

trait PropertyConverter {
  def init(args:Array[String]):Unit;
  def conv(form:Any):Any;
}

class BoolAtlasSprite extends PropertyConverter derives ReflectType {
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