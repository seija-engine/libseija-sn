package ui
import _root_.core.Entity
import scalanative.unsigned._
import ui.core.FFISeijaUI
import ui.core.EventNodeComponent;
import scala.collection.mutable;
import scala.scalanative.unsafe.CFuncPtr4
import scala.scalanative.unsafe.Ptr
import scala.scalanative.unsafe.CFuncPtr3
import scala.scalanative.posix.netinet.in
object EventType {
    val NONE = 0.toUInt;
    val TOUCH_START = 1.toUInt;
    val TOUCH_END = 2.toUInt;
    val MOUSE_ENTER = 4.toUInt;
    val MOUSE_LEAVE = 8.toUInt;
    val CLICK = 16.toUInt;
}

private case class EventInfo(val entity:Entity,callFunc:(Any) => Unit,args:Any);

object EventManager {
    var eventInfos:mutable.HashMap[Long,EventInfo] = mutable.HashMap();
    
    def register(entity:Entity,typ:UInt,callback:(Any) => Unit,args:Any = null):Boolean = {
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

    def unRegister(entity:Entity) = {
        if(this.eventInfos.contains(entity.id)) {
            this.eventInfos.remove(entity.id);
            FFISeijaUI.entityRemoveEventNode(_root_.core.App.worldPtr,entity.id);
        }
    }

    def update():Unit = {
        FFISeijaUI.readUIEvents(_root_.core.App.worldPtr,CFuncPtr3.fromScalaFunction(onReadEvent));
    }

    def onReadEvent(entityId:Long,typ:UInt,ptr:Ptr[Byte]):Unit = {
       if(this.eventInfos.contains(entityId)) {
          val info = this.eventInfos.get(entityId).get;
          info.callFunc(info.args);
       }
    }
}