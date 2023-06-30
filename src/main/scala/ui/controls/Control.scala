package ui.controls
import ui.controls.ControlTemplate
import core.reflect.*;
import core.logError;
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import scala.collection.mutable;
import ui.ElementNameScope
import scala.collection.mutable.HashMap
import scalanative.unsigned._;
import ui.EventType
import ui.visualState.ViewStates

class Control extends UIElement with ElementNameScope derives ReflectType {
    var template:Option[ControlTemplate] = None
    var nameDict:HashMap[String,UIElement] = HashMap.empty;

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

    override def Awake(): Unit = {
       if(this.template.isDefined) {
         this.template.get.Awake();
         val typInfo = Assembly.getTypeInfo(this);
         this.template.get.applyType(typInfo);
       }
       super.Awake();
    }

    override def OnEnter(): Unit = {
        this.createBaseEntity(true);
        this.loadControlTemplate();
    }

    override protected def onViewStateChanged(changeGroup: String, newState: String): Unit = {
      super.onViewStateChanged(changeGroup,newState);
      if(this.template.isDefined) {
        val visualGroup = this.template.get.visualStateGroups.getGroup(changeGroup);
        if(visualGroup.isEmpty) return;
        this.applyVisualGroup(visualGroup.get,newState,Some(this));
      }
    }

    protected def loadControlTemplate(): Unit = {
       println(s"${this} ${this.template}");
       this.findTemplate().foreach {t => 
          t.LoadContent(this,Some(this)).logError().foreach {elem => 
            this.addChild(elem);
            println(s"${this} add child${elem}");
          }
       }
    }

    def findTemplate():Option[ControlTemplate] = {
       if(this.template.isDefined) {
         return Some(this.template.get);
       }
       None
    }

    def getScopeElement(name:String):Option[UIElement] = { this.nameDict.get(name) }

    override def setScopeElement(name: String, elem: UIElement): Unit = {
      this.nameDict.put(name,elem);
    }

    protected def processViewStates(typ:UInt,args:Any):Unit = {
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
          if(this.IsPressed) {
            this.IsPressed = false;
          }
          this.updateVisualState();
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
}