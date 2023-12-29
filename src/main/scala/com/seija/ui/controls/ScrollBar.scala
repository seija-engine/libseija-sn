package com.seija.ui.controls
import com.seija.core.reflect.*
import com.seija.ui.core.Orientation
import com.seija.ui.event.{RouteEventArgs,RouteEvent}
import com.seija.ui.visualState.ViewStates

class ScrollBar extends RangeBase derives ReflectType {
    var _orientation: Orientation = Orientation.Horizontal
    var _barLength:Float = 50f

    //region Setter

    def orientation: Orientation = _orientation
    def orientation_=(value: Orientation): Unit = {
        _orientation = value; this.callPropertyChanged("orientation");
    }
    def barLength:Float = this._barLength
    def barLength_=(value:Float):Unit = {
      this._barLength = value;callPropertyChanged("barLength")
    }
    
    //endregion

    private var track:Option[Track] = None
    override def Awake(): Unit = {
      super.Awake()
    }
    override def OnEnter(): Unit = {
        this.updateVisualState()
        this.createBaseEntity(true)
        this.loadControlTemplate()
        this.routeEventController.addEvent(Thumb.OnDragEvent,this.onThumbDrag)
    }

    override def Enter(): Unit = {
      super.Enter()
      this.track = this.getScopeElement("PART_Track").map(_.asInstanceOf[Track])
    }

    private var _cacheScrollEvent:ScrollValueChangedEventArgs = ScrollValueChangedEventArgs(Orientation.Horizontal,0)
    protected def onThumbDrag(args:RouteEventArgs):Unit = {
      args.handled = true
      if(this.track.isEmpty) return
      val dragArgs = args.asInstanceOf[ThumbOnDragArgs]
      val deltaValue = this.track.get.valueFormDistance(dragArgs.deltaX,dragArgs.deltaY)
      val newValue = this.clipValue(this._value + deltaValue)
      this.value = newValue
      this.sendScrollEvent(this._value)
    }

    private def sendScrollEvent(value:Float):Unit = {
      this._cacheScrollEvent.handled = false
      this._cacheScrollEvent.value = value
      this._cacheScrollEvent.ori = this._orientation
      this.routeEventController.fireEvent(this._cacheScrollEvent)
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

    override def Exit(): Unit = {
      super.Exit()
      this.routeEventController.removeEvent(Thumb.OnDragEvent)
    }
}

class ScrollValueChangedEventArgs(var ori:Orientation,var value:Float) extends RouteEventArgs(ScrollBar.ScrollValueChanged,false)

object ScrollBar {
  val ScrollValueChanged: RouteEvent = RouteEvent("ScrollValueChanged",classOf[ScrollBar])
}