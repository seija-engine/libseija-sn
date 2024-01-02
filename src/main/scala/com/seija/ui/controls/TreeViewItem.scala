package com.seija.ui.controls
import com.seija.core.reflect.ReflectType;
import com.seija.ui.visualState.ViewStates
import com.seija.ui.event.EventManager
import com.seija.ui.event.EventType
import scalanative.unsigned.*
class TreeViewItem extends HeaderedItemsControl derives ReflectType {
    var _IsExpanded:Boolean = false;
    def IsExpanded:Boolean = this._IsExpanded
    def IsExpanded(value:Boolean):Unit = {
        this._IsExpanded = value; callPropertyChanged("IsExpanded")
    }
    
    private def headerElement:Option[UIElement] = this.nameDict.get("PART_Header")

    override def Enter(): Unit = {
        this.setViewState(ViewStates.SelectionStates,ViewStates.Unselected)
        super.Enter();
        this.headerElement.foreach {header =>
            EventManager.register(header.getEntity().get,EventType.ALL_TOUCH,false,true,this.OnElementEvent)
        }
        this.updateVisualState()
    }

    def OnElementEvent(typ:UInt,px:Float,py:Float,args:Any):Unit = {
        this.processViewStates(typ,args);
        val zero = 0.toUInt
        if((typ & EventType.TOUCH_START) != zero) {
            println(s"click:${this.entity.get}")
        }
    }

    override def onPropertyChanged(propertyName: String): Unit = {
        propertyName match
            case "IsExpanded" => { this.updateVisualState() }
            case _ =>
    }

    override def updateVisualState(): Unit = {
        super.updateVisualState()
        if(this._IsExpanded) {
            this.setViewState(ViewStates.ExpansionStates,ViewStates.Expanded)
        } else {
            this.setViewState(ViewStates.ExpansionStates,ViewStates.Collapsed)
        }
        if(this._hasItems) {
            this.setViewState(ViewStates.HasItemsStates,ViewStates.HasItems)
        } else {
            this.setViewState(ViewStates.HasItemsStates,ViewStates.NoItems)
        }
    }

    override def Exit(): Unit = {
        this.headerElement.foreach {header =>
            EventManager.unRegister(header.getEntity().get)
        }
        super.Exit()
    }
}