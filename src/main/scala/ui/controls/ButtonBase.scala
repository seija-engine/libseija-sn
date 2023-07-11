package ui.controls
import core.reflect.*;
import scalanative.unsigned._
import ui.visualState.ViewStates
import ui.command.ICommand
import ui.event.{EventManager, EventType}

class ButtonBase extends ContentControl derives ReflectType {
    var command:Option[ICommand] = None;
    var commandParams:Any = null;
    
    var _IsPressed:Boolean = false;
    def IsPressed:Boolean = _IsPressed;
    def IsPressed_=(value:Boolean):Unit = { _IsPressed = value; this.callPropertyChanged("IsPressed",this); }

    override def OnEnter(): Unit = {
       val thisEntity = this.createBaseEntity(true);
       this.loadControlTemplate();
       val events = EventType.ALL_TOUCH | EventType.ALL_MOUSE;
       EventManager.register(thisEntity,events,this.OnElementEvent);
       updateVisualState();
    }

    protected def OnElementEvent(typ:UInt,args:Any):Unit = {
       this.processViewStates(typ,args);
       val zero = 0.toUInt;
       if((typ & EventType.TOUCH_START) != zero) {
          this.IsPressed = true;
          this.onStartPressed();
       }
       if((typ & EventType.TOUCH_END) != zero) {
         this.IsPressed = false;
          this.onEndPressed();
       }
       if((typ & EventType.MOUSE_LEAVE) != zero) {
          this.IsHover = false;
          if(this.IsPressed) {
            this.IsPressed = false;
            this.onEndPressed();
          }
       }
       if((typ & EventType.CLICK) != zero) {
         this.onClick();
       }
    }

    protected def onStartPressed():Unit = { }

    protected def onEndPressed():Unit = { }

    protected def onClick():Unit = { this.callCommand(); }

    protected def callCommand():Unit = {
      this.command.foreach { cmd => cmd.Execute(this.commandParams); }
    }

    override def Exit(): Unit = {
        super.Exit();
        EventManager.unRegister(this.entity.get);
    }
}