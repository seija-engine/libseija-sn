package ui.controls
import core.reflect.*;
import ui.EventManager
import ui.EventType
import scalanative.unsigned._
import ui.visualState.ViewStates

class ButtonBase extends ContentControl derives ReflectType {
    var _IsPressed:Boolean = false;
    def IsPressed:Boolean = _IsPressed;
    def IsPressed_=(value:Boolean):Unit = {
        _IsPressed = value;
        this.callPropertyChanged("IsPressed",this);
    }

    var _IsMouseOver:Boolean = false;
    def IsMouseOver:Boolean = _IsMouseOver;
    def IsMouseOver_=(value:Boolean):Unit = {
        _IsMouseOver = value;
        this.callPropertyChanged("IsMouseOver",this);
    }
    
    override def OnEnter(): Unit = {
       val thisEntity = this.createBaseEntity(true);
       this.loadControlTemplate();
       val events = EventType.ALL;
       EventManager.register(thisEntity,events,this.OnElementEvent);
       updateVisualState();
    }

    protected def OnElementEvent(typ:UInt,args:Any):Unit = {
       val zero = 0.toUInt;
       if((typ & EventType.TOUCH_START) != zero) {
          this.IsPressed = true;
          this.updateVisualState();
       }
       if((typ & EventType.TOUCH_END) != zero) {
          this.IsPressed = false;
          this.updateVisualState();
       }
       if((typ & EventType.MOUSE_ENTER) != zero) {
          this.IsMouseOver = true;
          this.updateVisualState();
       }
       if((typ & EventType.MOUSE_LEAVE) != zero) {
          this.IsMouseOver = false;
          this.updateVisualState();
       }
       if((typ & EventType.CLICK) != zero) {
         
       }
    }

    def updateVisualState():Unit = {
       if(this._IsPressed) {
         this.setViewState(ViewStates.CommonStates,ViewStates.Pressed);
       } else if(this._IsMouseOver) {
          this.setViewState(ViewStates.CommonStates,ViewStates.MouseOver);
       } else {
          this.setViewState(ViewStates.CommonStates,ViewStates.Normal);
       }
    }

    override def onViewStateChanged(changeGroup: String, newState: String): Unit = {
       println(s"change group:${changeGroup} newState:${newState}")
       
    }

    override def Exit(): Unit = {
        EventManager.unRegister(this.entity.get);
    }
}