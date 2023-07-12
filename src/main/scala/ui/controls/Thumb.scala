package ui.controls
import core.reflect.*

import scalanative.unsigned.*
import input.Input
import math.Vector2
import math.Vector3
import transform.{RawTransform, Transform}
import transform.getPosition
import transform.setPosition
import ui.core.{FreeLayoutItem, ItemLayout}
import ui.event.{EventManager, EventType, RouteEvent, RouteEventArgs, RouteEventController}

class Thumb extends Control derives ReflectType {
    var OnBeginDragCall:Option[(Vector2) => Unit] = None;
    var OnDragCall:Option[(Vector2) => Unit] = None;
    var OnEndDragCall:Option[(Vector2) => Unit] = None;

    var curPos:Vector3 = Vector3.zero;
    override def OnEnter(): Unit = {
        //println(s"Thumb Enter ${this.parent} ${this.style}" );
        val thisEntity = this.createBaseEntity(true);
        EventManager.register(thisEntity,EventType.ALL,this.OnElementEvent);
        this.loadControlTemplate()
        thisEntity.add[FreeLayoutItem]();
    }

    protected def OnElementEvent(typ:UInt,args:Any):Unit = {
        this.processViewStates(typ,args);
        val zero = 0.toUInt;
        if((typ & EventType.BEGIN_DRAG) != zero) {
            this.OnBeginDrag();
        }
        if((typ & EventType.DRAG) != zero) {
            this.OnDrag();
        }
        if((typ & EventType.END_DRAG) != zero) {
            this.OnEndDrag();
        }
    }

    protected def OnBeginDrag():Unit = {
      this.OnBeginDragCall.foreach(_(Input.getMousePos()))
      this.routeEventController.fireEvent(ThumbDragStartArgs())
      this.IsActive = true;
      this.updateVisualState();
    }

    protected def OnDrag():Unit = {
      val delta = Input.getMoveDelta();
      this.OnDragCall.foreach(_(delta))
      this.routeEventController.fireEvent(ThumbOnDragArgs(delta))
    }

    protected def OnEndDrag():Unit = {
      this.OnEndDragCall.foreach(_(Input.getMousePos()))
      this.routeEventController.fireEvent(ThumbDragEndArgs())
      this.IsActive = false;
      this.updateVisualState();
    }

    override protected def processViewStates(typ: UInt, args: Any): Unit = {
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
          this.updateVisualState();
       }
    }

    override def onPropertyChanged(propertyName: String): Unit = {
      propertyName match
        case "width" => {
          this.entity.foreach { v =>
            val rawLayout = v.get[ItemLayout]();
            rawLayout.setWidth(this._width);
          }
        }
        case "height" => {
          this.entity.foreach { v =>
            val rawLayout = v.get[ItemLayout]();
            rawLayout.setHeight(this._height);
          }
        }
        case _ =>
   }

    override def clone():Thumb = {
      val newThumb = super.clone().asInstanceOf[Thumb]
      newThumb.setRouteEventElem(newThumb)
      newThumb
    }

    override def Exit(): Unit = {
        EventManager.unRegister(this.entity.get);
        super.Exit();
    }
}

class ThumbDragStartArgs extends RouteEventArgs(Thumb.StartDragEvent,false)
class ThumbDragEndArgs extends RouteEventArgs(Thumb.EndDragEvent,false)
class ThumbOnDragArgs(val delta:Vector2) extends RouteEventArgs(Thumb.OnDragEvent,false)

object Thumb {
  val StartDragEvent:RouteEvent = RouteEvent("StartDrag",classOf[Thumb])
  val OnDragEvent:RouteEvent = RouteEvent("OnDrag",classOf[Thumb])
  val EndDragEvent:RouteEvent = RouteEvent("EndDrag",classOf[Thumb])
}