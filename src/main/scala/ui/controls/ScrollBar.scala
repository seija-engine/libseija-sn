package ui.controls
import core.reflect.*;
import ui.core.Orientation
import ui.visualState.ViewStates

class ScrollBar extends RangeBase derives ReflectType {
    var _orientation: Orientation = Orientation.Horizontal;
    def orientation: Orientation = _orientation;
    def orientation_=(value: Orientation): Unit = {
        _orientation = value; this.callPropertyChanged("orientation", this);
    }

    override def OnEnter(): Unit = {
        this.updateVisualState();
        this.createBaseEntity(true);
        this.loadControlTemplate();
    }

    override def updateVisualState(): Unit = {
        this._orientation match {
            case Orientation.Horizontal => {
                this.setViewState(ViewStates.OrientationStates,ViewStates.Horizontal);
            } 
            case Orientation.Vertical => {
                this.setViewState(ViewStates.OrientationStates,ViewStates.Vertical);
            }
        }
    }
}