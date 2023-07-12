package ui.controls
import core.reflect.*
import ui.controls.Track
import ui.core.Orientation
import ui.event.RouteEventArgs
import ui.visualState.ViewStates
class Slider extends RangeBase derives ReflectType {
    protected var _orientation: Orientation = Orientation.Horizontal
    private var track:Option[Track] = None

    //region Setter

    def orientation: Orientation = _orientation;
    def orientation_=(value: Orientation): Unit = {
        _orientation = value; this.callPropertyChanged("orientation", this);
    }
    //endregion

    override def Awake(): Unit = {
      super.Awake()
      this.routeEventController.addEvent(Thumb.OnDragEvent,this.onDragThumb)
    }

    override def OnEnter(): Unit = {
        this.updateVisualState();
        this.createBaseEntity(true)
        this.loadControlTemplate();
    }

    override def Enter(): Unit = {
      super.Enter()
      this.track = this.getScopeElement("PART_Track").map(_.asInstanceOf[Track])
    }

    protected def onDragThumb(args:RouteEventArgs):Unit = {
      args.handled = true
      val onDragArgs = args.asInstanceOf[ThumbOnDragArgs]
      //

    }

    protected def updateValue(f:Float):Unit = {

    }

    override def onPropertyChanged(propertyName: String): Unit = {
      super.onPropertyChanged(propertyName)
      propertyName match
        case "value" => { this.updateValue(this._value) }
        case _ =>
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
/*
  ScrollViewer    OnContentDirty() { this.thumbLength = 123 }
    ScrollContent --HandlePostLayout() { if(dirty) { fire(ContentDirty) } }
    ScrollBar   {Binding ThumbLength}
      Track     {Binding ThumbLength} => { setLayoutW(); postDirtyEntity!() }
        Thumb
*/



















