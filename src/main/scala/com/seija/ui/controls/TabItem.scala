package com.seija.ui.controls
import com.seija.core.reflect.ReflectType

import scala.scalanative.unsigned.UInt
import com.seija.ui.event.{EventManager, EventType, RouteEventArgs}

import scalanative.unsigned.*
import com.seija.ui.visualState.ViewStates
import javax.swing.text.View
import com.seija.ui.visualState.ViewStates.CommonStates
class TabItem extends HeaderedContentControl derives ReflectType {
    def IsSelected:Boolean = this.GetPropValue(Selector.IsSelectedProperty).asInstanceOf[Boolean]
    def IsSelected_=(value:Boolean):Unit = {
        this.SetPropValue(Selector.IsSelectedProperty,value)
        callPropertyChanged("IsSelected",this)
        OnIsSelectedChanged()
    }

    override def Enter(): Unit = {
        this.setViewState(ViewStates.FocusStates,ViewStates.Unfocused)
        super.Enter()
        val thisEntity = this.getEntity().get
        EventManager.register(thisEntity,EventType.ALL_MOUSE | EventType.ALL_TOUCH,this.OnElementEvent)
        this.updateVisualState()
    }

    protected def OnElementEvent(typ:UInt,px:Float,py:Float,args:Any):Unit = {
       val zero = 0.toUInt
       if((typ & EventType.TOUCH_START) != zero) {
        if(!this.IsSelected) {
            this.IsSelected = true
        }
       }
    }

    override def onPropertyChanged(propertyName: String): Unit = {
      propertyName match
        case Selector.IsSelectedProperty.propKey => 
          this.updateVisualState()
        case _ => 
      
    }

    override def updateVisualState(): Unit = {
      super.updateVisualState()
      if(this.IsSelected) {
        this.setViewState(ViewStates.FocusStates,ViewStates.Focused)
      } else {
        this.setViewState(ViewStates.FocusStates,ViewStates.Unfocused)
      }
    }

    private def OnIsSelectedChanged():Unit = {
       if(this.IsSelected) {
         this.OnSelected(SelectEventArgs(this))
       } else {
         this.OnUnselected(UnselectEventArgs(this))
       }
       this.updateVisualState()
    }

    def OnSelected(args:RouteEventArgs):Unit = {
      this.routeEventController.fireEvent(args)
    }

    def OnUnselected(args:RouteEventArgs):Unit = {
      this.routeEventController.fireEvent(args)
    }
}