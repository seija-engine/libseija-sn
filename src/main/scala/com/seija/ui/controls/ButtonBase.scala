package com.seija.ui.controls
import com.seija.core.reflect.*;
import scalanative.unsigned._
import com.seija.ui.visualState.ViewStates
import com.seija.ui.command.ICommand
import com.seija.ui.event.{EventManager, EventType}

class ButtonBase extends ContentControl derives ReflectType {
    var command:Option[ICommand] = None;
    var commandParams:Any = null;
    
    var _IsPressed:Boolean = false;
    def IsPressed:Boolean = _IsPressed;
    def IsPressed_=(value:Boolean):Unit = { _IsPressed = value; this.callPropertyChanged("IsPressed"); }

    override def OnEnter(): Unit = {
       val thisEntity = this.createBaseEntity(true);
       this.loadControlTemplate();
       val events = EventType.ALL_TOUCH | EventType.ALL_MOUSE;
       EventManager.register(thisEntity,events,false,this.OnElementEvent)
       updateVisualState();
    }

    protected def OnElementEvent(typ:UInt,px:Float,py:Float,args:Any):Unit = {
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

    override def onPropertyChanged(propertyName: String): Unit = {
      propertyName match
         case "IsEnabled" => if(this.isEntered) this.updateVisualState()
         case _ =>
    }
    override def Exit(): Unit = {
        super.Exit();
        EventManager.unRegister(this.entity.get);
    }
}