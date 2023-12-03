package com.seija.ui.controls
import com.seija.core.reflect.*
import com.seija.ui.controls.Track
import com.seija.ui.core.Orientation
import com.seija.ui.event.RouteEventArgs
import com.seija.ui.visualState.ViewStates

import scala.annotation.unused

class Slider extends RangeBase derives ReflectType {
    protected var _orientation: Orientation = Orientation.Horizontal
    private var track:Option[Track] = None
    protected var _isInteger:Boolean = false
    private var _realValue:Float = 0
    //region Setter

    def orientation: Orientation = _orientation
    def orientation_=(value: Orientation): Unit = {
        _orientation = value; this.callPropertyChanged("orientation", this);
    }
    def isInteger:Boolean = this._isInteger
    def isInteger_=(value:Boolean):Boolean = {
      this._isInteger = value; callPropertyChanged("isInteger",this); this._isInteger
    }

    override def value_=(num: Float): Unit = {
      this._value = clipValue(num);callPropertyChanged("value",this)
      this._realValue = num
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
      if(this.track.isEmpty) return
      val onDragArgs = args.asInstanceOf[ThumbOnDragArgs]
      val deltaValue = this.track.get.valueFormDistance(onDragArgs.deltaX,onDragArgs.deltaY)
      this._realValue = this._realValue + deltaValue
      val newValue = this.clipValue(this._realValue)
      if(newValue != this._value) {
        this._value = newValue
        callPropertyChanged("value",this)
      }
    }

     override protected def clipValue(newValue:Float):Float = {
      val clipValue = if(newValue < this._minValue) {
        this._minValue
      } else if(newValue > this._maxValue) {
        this._maxValue
      } else {
        newValue
      }
      if(_isInteger) { clipValue.round.toFloat } else { clipValue }
    }



    override def onPropertyChanged(propertyName: String): Unit = {
      super.onPropertyChanged(propertyName)
      propertyName match
        case "value" =>
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



















