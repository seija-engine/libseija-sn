package ui.controls
import core.reflect.ReflectType;
import ui.core.StackLayout as CoreStackLayout;
import ui.core.Orientation
import core.Entity
import transform.Transform;
import ui.core.Rect2D;

class StackLayout extends BaseLayout derives ReflectType {
    var _orientation:Orientation = Orientation.Vertical;
    def orientation:Orientation = _orientation;
    def orientation_=(value:Orientation):Unit = {
        _orientation = value; this.callPropertyChanged("orientation",this);
    }
    var _spacing:Float = 0;
    def spacing:Float = _spacing;
    def spacing_=(value:Float):Unit = {
        _spacing = value; this.callPropertyChanged("spacing",this);
    }
    
    override def OnEnter():Unit = {
        
        val parentEntity = this.parent.flatMap(_.getEntity());
        val entity = Entity.spawnEmpty()
          .add[Transform](_.parent = parentEntity)
          .add[Rect2D]()
          .add[CoreStackLayout](v => {
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