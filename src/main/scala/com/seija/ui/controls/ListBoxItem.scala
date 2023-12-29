package com.seija.ui.controls
import com.seija.core.reflect.ReflectType;
import com.seija.ui.event.EventManager
import com.seija.ui.event.EventType
import scala.scalanative.unsigned.UInt
import scalanative.unsigned.*
import com.seija.ui.visualState.ViewStates
class ListBoxItem extends ContentControl derives ReflectType {
    def IsSelected:Boolean = this.GetPropValue(Selector.IsSelectedProperty).asInstanceOf[Boolean]
    def IsSelected_=(value:Boolean):Unit = {
        this.SetPropValue(Selector.IsSelectedProperty,value)
        callPropertyChanged("IsSelected")
        this.OnIsSelectedChanged()
    }

    override def Enter(): Unit = {
        this.setViewState(ViewStates.SelectionStates,ViewStates.Unselected)
        super.Enter();
        val thisEntity = this.getEntity().get
        EventManager.register(thisEntity,EventType.ALL_MOUSE | EventType.ALL_TOUCH,false,this.OnElementEvent)
        this.updateVisualState()
    }

    def OnElementEvent(typ:UInt,px:Float,py:Float,args:Any):Unit = {
        this.processViewStates(typ,args);
        val zero = 0.toUInt
        if((typ & EventType.TOUCH_START) != zero) {
            if(!this.IsSelected) { 
                this.IsSelected = true 
            }
        }
    }

    def OnIsSelectedChanged():Unit = {
        if(this.IsSelected) {
            this.routeEventController.fireEvent(SelectEventArgs(this))
        } else {
            this.routeEventController.fireEvent(UnselectEventArgs(this))
        }
    }

    override def onPropertyChanged(propertyName: String): Unit = {
        propertyName match
            case Selector.IsSelectedProperty.propKey => {
                this.updateVisualState()
            }
            case _ => 
    }
    
    override def updateVisualState(): Unit = {
        super.updateVisualState()
        if(this.IsSelected) {
            this.setViewState(ViewStates.SelectionStates,ViewStates.Selected)
        } else {
            this.setViewState(ViewStates.SelectionStates,ViewStates.Unselected)
        }
    }

}