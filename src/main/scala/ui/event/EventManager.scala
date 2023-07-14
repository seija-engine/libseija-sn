package ui.event

import _root_.core.Entity
import ui.core.EventNode.EventNodeComponent
import ui.core.FFISeijaUI

import scala.collection.mutable
import scala.scalanative.unsafe.{CFuncPtr3, CFuncPtr4, Ptr}
import scala.scalanative.unsigned.*
object EventType {
    val NONE: UInt = 0.toUInt;
    val TOUCH_START: UInt = 1.toUInt;
    val TOUCH_END: UInt = 2.toUInt;
    val MOUSE_ENTER: UInt = 4.toUInt;
    val MOUSE_LEAVE: UInt = 8.toUInt;
    val CLICK: UInt = 16.toUInt;
    val BEGIN_DRAG: UInt = (1 << 5).toUInt
    val DRAG: UInt = (1 << 6).toUInt
    val END_DRAG: UInt = (1 << 7).toUInt

    val ALL_TOUCH: UInt = TOUCH_START | TOUCH_END | CLICK;
    val ALL_MOUSE: UInt = MOUSE_ENTER | MOUSE_LEAVE;
    val ALL_DRAG: UInt = BEGIN_DRAG | DRAG | END_DRAG;
    val ALL: UInt = ALL_TOUCH | ALL_MOUSE | ALL_DRAG;
}

private case class EventInfo(val entity:Entity,callFunc:(UInt,Float,Float,Any) => Unit,args:Any);

object EventManager {
    var eventInfos:mutable.HashMap[Long,EventInfo] = mutable.HashMap();
    
    def register(entity:Entity,typ:UInt,callback:(UInt,Float,Float,Any) => Unit,args:Any = null):Boolean = {
        if(this.eventInfos.contains(entity.id)) {
            return false;
        }
        entity.add[ui.core.EventNode](ev => {
            ev.eventType = typ;
        });
        val info = EventInfo(entity,callback,args);
        this.eventInfos.put(entity.id,info);
        true
    }

    def unRegister(entity:Entity): Unit = {
        if(this.eventInfos.contains(entity.id)) {
            this.eventInfos.remove(entity.id);
            FFISeijaUI.entityRemoveEventNode(_root_.core.App.worldPtr,entity.id);
        }
    }

    def update():Unit = {
        FFISeijaUI.readUIEvents(_root_.core.App.worldPtr,CFuncPtr4.fromScalaFunction(onReadEvent));
    }

    def onReadEvent(entityId:Long,typ:UInt,px:Float,py:Float):Unit = {
       if(this.eventInfos.contains(entityId)) {
          val info = this.eventInfos(entityId);
          info.callFunc(typ,px,-py,info.args);
       }
    }
}