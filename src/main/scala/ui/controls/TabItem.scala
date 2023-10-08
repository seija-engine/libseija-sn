package ui.controls
import core.reflect.ReflectType

import scala.scalanative.unsigned.UInt
import ui.event.{EventManager, EventType, RouteEventArgs}

import scalanative.unsigned.*
class TabItem extends HeaderedContentControl derives ReflectType {
    var _IsSelected:Boolean = false
    def IsSelected:Boolean = this._IsSelected
    def IsSelected_=(value:Boolean):Unit = {
        this._IsSelected = value
        callPropertyChanged("IsSelected",this)
        OnIsSelectedChanged()
    }

    override def Enter(): Unit = {
        super.Enter()
        val thisEntity = this.getEntity().get
        EventManager.register(thisEntity,EventType.ALL_MOUSE | EventType.ALL_TOUCH,this.OnElementEvent)
    }

    protected def OnElementEvent(typ:UInt,px:Float,py:Float,args:Any):Unit = {
       val zero = 0.toUInt
       if((typ & EventType.TOUCH_START) != zero) {
        if(!this._IsSelected) {
            this.IsSelected = true
        }
       }
    }

    private def OnIsSelectedChanged():Unit = {
       if(this._IsSelected) {
         this.OnSelected(RouteEventArgs(Selector.SelectedEvent))
       } else {
         this.OnUnselected(RouteEventArgs(Selector.UnselectedEvent))
       }
    }

    def OnSelected(args:RouteEventArgs):Unit = {
      this.routeEventController.fireEvent(args)
    }
    def OnUnselected(args:RouteEventArgs):Unit = {
      this.routeEventController.fireEvent(args)
    }

}