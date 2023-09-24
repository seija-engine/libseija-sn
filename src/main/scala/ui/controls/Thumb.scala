package ui.controls
import core.reflect.*

import scalanative.unsigned.*
import input.Input
import math.Vector2
import math.Vector3
import transform.{RawTransform, Transform}
import transform.getLocalPosition
import ui.core.{FreeLayoutItem, ItemLayout}
import ui.event.{EventManager, EventType, RouteEvent, RouteEventArgs, RouteEventController}
import ui.LayoutUtils

class Thumb extends Control derives ReflectType {
    override def OnEnter(): Unit = {
        val thisEntity = this.createBaseEntity(true);
        EventManager.register(thisEntity,EventType.ALL,this.OnElementEvent)
        this.loadControlTemplate()
        thisEntity.add[FreeLayoutItem]();
    }

    protected def OnElementEvent(typ:UInt,px:Float,py:Float,args:Any):Unit = {
        this.processViewStates(typ,args);
        val zero = 0.toUInt;
        if((typ & EventType.BEGIN_DRAG) != zero) {
            this.OnBeginDrag()
        }
        if((typ & EventType.DRAG) != zero) {
            this.OnDrag(px,py)
        }
        if((typ & EventType.END_DRAG) != zero) {
            this.OnEndDrag();
        }
    }

    protected def OnBeginDrag():Unit = {
      this.routeEventController.fireEvent(ThumbDragStartArgs())
      this.IsActive = true;
      this.updateVisualState();
    }

    protected def OnDrag(dx:Float,dy:Float):Unit = {
      this.routeEventController.fireEvent(ThumbOnDragArgs(dx,dy))
    }

    protected def OnEndDrag():Unit = {
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
            val frame = _root_.core.Time.getFrameCount();
            if(LayoutUtils.isInPostLayout) {
              val rawLayout = v.get[ItemLayout]();
              rawLayout.setHeight(this._height);
              LayoutUtils.addPostLayoutDirtyEntity(v);
            }
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
class ThumbOnDragArgs(val deltaX:Float,val deltaY:Float) extends RouteEventArgs(Thumb.OnDragEvent,false)

object Thumb {
  val StartDragEvent:RouteEvent = RouteEvent("StartDrag",classOf[Thumb])
  val OnDragEvent:RouteEvent = RouteEvent("OnDrag",classOf[Thumb])
  val EndDragEvent:RouteEvent = RouteEvent("EndDrag",classOf[Thumb])
}