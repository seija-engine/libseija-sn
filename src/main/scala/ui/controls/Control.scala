package ui.controls
import ui.controls.ControlTemplate
import core.reflect.*;
import core.logError;
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import scala.collection.mutable;
import ui.ElementNameScope
import ui.event.EventType
import scala.collection.mutable.HashMap
import scalanative.unsigned._;
import ui.visualState.ViewStates

class Control extends UIElement with ElementNameScope derives ReflectType {
    var template:Option[ControlTemplate] = None
    var nameDict:HashMap[String,UIElement] = HashMap.empty;

    var _IsActive:Boolean = false;
    def IsActive:Boolean = _IsActive;
    def IsActive_=(value:Boolean):Unit = {
        _IsActive = value;
        this.callPropertyChanged("IsActive",this);
    }

    var _IsHover:Boolean = false;
    def IsHover:Boolean = _IsHover;
    def IsHover_=(value:Boolean):Unit = {
        _IsHover = value;
        this.callPropertyChanged("IsHover",this);
    }
    

    override def OnEnter(): Unit = {
        this.createBaseEntity(true);
        this.loadControlTemplate();
    }

    override protected def onViewStateChanged(changeGroup: String, newState: String): Unit = {
      super.onViewStateChanged(changeGroup,newState);
      if(this.template.isDefined) {
         this.template.get.vsm.onViewStateChanged(this,changeGroup,newState,Some(this))
      }
    }

    protected def loadControlTemplate(): Unit = {
       this.findTemplate().foreach {t => 
          t.LoadContent(this,Some(this)).logError().foreach {elem => 
            this.addChild(elem);
          }
       }
    }

    def findTemplate():Option[ControlTemplate] = {
       if(this.template.isDefined) {
         return Some(this.template.get);
       }
       None
    }

    def getScopeElement(name:String):Option[UIElement] = {
      this.nameDict.get(name) 
    }

    override def setScopeElement(name: String, elem: UIElement): Unit = {
      this.nameDict.put(name,elem);
    }

    protected def processViewStates(typ:UInt,args:Any):Unit = {
       val zero = 0.toUInt;
       if((typ & EventType.TOUCH_START) != zero) {
          this.IsActive = true;
          this.updateVisualState();
       }
       if((typ & EventType.TOUCH_END) != zero) {
          this.IsActive = false;
          this.updateVisualState();
       }
       if((typ & EventType.MOUSE_ENTER) != zero) {
          this.IsHover = true;
          this.updateVisualState();
       }
       if((typ & EventType.MOUSE_LEAVE) != zero) {
          this.IsHover = false;
          if(this.IsActive) {
            this.IsActive = false;
          }
          this.updateVisualState();
       }
    }

    def updateVisualState():Unit = {
       if(this._IsActive) {
         this.setViewState(ViewStates.CommonStates,ViewStates.Pressed);
       } else if(this._IsHover) {
          this.setViewState(ViewStates.CommonStates,ViewStates.MouseOver);
       } else {
          this.setViewState(ViewStates.CommonStates,ViewStates.Normal);
       }
    }

    override def clone():Control = {
       var newControl = super.clone().asInstanceOf[Control];
       newControl.nameDict = mutable.HashMap.empty;
       newControl
    }
}