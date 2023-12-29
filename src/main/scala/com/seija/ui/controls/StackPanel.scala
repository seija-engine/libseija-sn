package com.seija.ui.controls
import com.seija.core.reflect.*;
import scala.collection.mutable.ListBuffer
import com.seija.ui.core.StackLayout
import com.seija.ui.core.Orientation
import com.seija.ui.ContentProperty
@ContentProperty("children")
class StackPanel extends Panel derives ReflectType {
    var _orientation:Orientation = Orientation.Vertical;
    var _spacing:Float = 0;
    
    def orientation:Orientation = _orientation;
    def orientation_=(value:Orientation):Unit = {
        _orientation = value; this.callPropertyChanged("orientation");
    }
    def spacing:Float = _spacing;
    def spacing_=(value:Float):Unit = {
        _spacing = value; this.callPropertyChanged("spacing");
    }

    override def OnEnter(): Unit = {
        val entity = this.createBaseEntity(false);
        entity.add[StackLayout](v => {
            v.common.hor = this._hor;
            v.common.ver = this._ver;
            v.common.uiSize.width = this._width;
            v.common.padding = this._padding;
            v.common.margin = this._margin;
            v.common.uiSize.height = this._height;
            v.orientation = this._orientation;
            v.spacing = this._spacing;
        })
        this.checkAddCanvas()
        this.handleItemsHost()
    }
}