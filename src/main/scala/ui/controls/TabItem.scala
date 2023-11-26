package ui.controls
import core.reflect.ReflectType

import scala.scalanative.unsigned.UInt
import ui.event.{EventManager, EventType, RouteEventArgs}

import scalanative.unsigned.*
class TabItem extends HeaderedContentControl derives ReflectType {
    def IsSelected:Boolean = this.GetPropValue(Selector.IsSelectedProperty).asInstanceOf[Boolean]
    def IsSelected_=(value:Boolean):Unit = {
        this.SetPropValue(Selector.IsSelectedProperty,value)
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
        if(!this.IsSelected) {
            this.IsSelected = true
        }
       }
    }

    private def OnIsSelectedChanged():Unit = {
       if(this.IsSelected) {
         this.OnSelected(SelectEventArgs(this))
       } else {
         this.OnUnselected(UnselectEventArgs(this))
       }
    }

    def OnSelected(args:RouteEventArgs):Unit = {
      this.routeEventController.fireEvent(args)
    }

    def OnUnselected(args:RouteEventArgs):Unit = {
      this.routeEventController.fireEvent(args)
    }
}