package ui.controls
import core.reflect.*;
import ui.EventManager;
import ui.EventType;
import scalanative.unsigned._;

class Thumb extends Control derives ReflectType {
    override def OnEnter(): Unit = {
        val thisEntity = this.createBaseEntity(false);
        EventManager.register(thisEntity,EventType.ALL,this.OnElementEvent);
    }

    protected def OnElementEvent(typ:UInt,args:Any):Unit = {
        val zero = 0.toUInt;
        if((typ & EventType.TOUCH_START) != zero) {
        }
        if((typ & EventType.TOUCH_END) != zero) {
        }
    }

    protected def OnTouchStart():Unit = {

    }

    protected def OnTouchEnd():Unit = {

    }

    protected def OnTouchMove():Unit = {

    }

    override def Exit(): Unit = {
        EventManager.unRegister(this.entity.get);
        super.Exit();
    }
}
