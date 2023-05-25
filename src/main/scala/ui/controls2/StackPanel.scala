package ui.controls2
import core.reflect.*;
import core.xml.XmlElement
import scala.collection.mutable.ListBuffer
import ui.core.StackLayout
import ui.core.Orientation

class StackPanel extends Panel derives ReflectType {
    var _orientation:Orientation = Orientation.Vertical;
    var _spacing:Float = 0;
    
    def orientation:Orientation = _orientation;
    def orientation_=(value:Orientation):Unit = {
        _orientation = value; this.callPropertyChanged("orientation",this);
    }
    def spacing:Float = _spacing;
    def spacing_=(value:Float):Unit = {
        _spacing = value; this.callPropertyChanged("spacing",this);
    }

    override def OnEnter(): Unit = {
        val entity = this.createBaseEntity(false);
        entity.add[StackLayout](v => {
            v.common.hor = this._hor;
            v.common.ver = this._ver;
            v.common.uiSize.width = this._width;
            v.common.padding = this._padding;
            v.common.uiSize.height = this._height;
            v.orientation = this._orientation;
            v.spacing = this._spacing;
        })
        this.entity = Some(entity);
    }
}