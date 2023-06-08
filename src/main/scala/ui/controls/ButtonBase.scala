package ui.controls
import core.reflect.*;
import ui.EventManager
import ui.EventType
import scalanative.unsigned._

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
          println("state pressed");
       } else if(this._IsMouseOver) {
          println("state mouse over");
       } else {
          println("state normal");
       }
    }

    override def Exit(): Unit = {
        EventManager.unRegister(this.entity.get);
    }
}