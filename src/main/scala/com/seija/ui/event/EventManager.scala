package com.seija.ui.event

import com.seija.core.Entity
import com.seija.ui.core.EventNode.EventNodeComponent
import com.seija.ui.core.FFISeijaUI

import scala.collection.mutable
import scala.scalanative.unsafe.CFuncPtr4
import scala.scalanative.unsigned.*
import com.seija.core.{App, Entity}
import com.seija.input.KeyCode.B
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
    
    def register(entity:Entity,typ:UInt,useCapture:Boolean,callback:(UInt,Float,Float,Any) => Unit,args:Any = null,addEventNode:Boolean = true):Boolean = {
        if(this.eventInfos.contains(entity.id)) {
            return false;
        }
        if(addEventNode) {
            entity.add[com.seija.ui.core.EventNode](ev => {
                ev.eventType = typ;
                ev.useCapture = useCapture;
            });
        }
        val info = EventInfo(entity,callback,args);
        this.eventInfos.put(entity.id,info);
        true
    }

   

    def unRegister(entity:Entity,rmEventNode:Boolean = true): Unit = {
        if(this.eventInfos.contains(entity.id)) {
            this.eventInfos.remove(entity.id);
            if(rmEventNode) {
                FFISeijaUI.entityRemoveEventNode(com.seija.core.App.worldPtr,entity.id);
            }
        }
    }

    def update():Unit = {
        FFISeijaUI.readUIEvents(com.seija.core.App.worldPtr,CFuncPtr4.fromScalaFunction(onReadEvent));
    }

    def onReadEvent(entityId:Long,typ:UInt,px:Float,py:Float):Unit = {
       if(this.eventInfos.contains(entityId)) {
          val info = this.eventInfos(entityId);
          info.callFunc(typ,px,-py,info.args);
       }
    }
}