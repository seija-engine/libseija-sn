package com.seija.ui.controls
import com.seija.core.reflect.ReflectType
import com.seija.core.UpdateMgr
import com.seija.ui.core.FFISeijaUI
import com.seija.core.App;
import com.seija.ui.event.EventManager
import com.seija.ui.event.RouteEventArgs

class TextBox extends Control derives ReflectType {
    
    var _text:String = ""
    def text:String = this._text
    def text_=(value:String):Unit = this._text
     
    override def OnEnter(): Unit = {
        super.OnEnter()
        this.routeEventController.addEvent(InputText.ActiveEvent,this.onInputActiveChanged)
    }

    protected def onInputActiveChanged(args:RouteEventArgs):Unit = {
       val event = args.asInstanceOf[InputTextEventArgs]
       event.handled = true;
       this.IsActive = event.isActive;
       this.updateVisualState()
    }

    override def Exit(): Unit = {
        this.routeEventController.removeEvent(InputText.ActiveEvent)
        super.Exit()
    }
}