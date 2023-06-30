package ui.controls
import core.reflect.*;
import ui.EventManager;
import ui.EventType;
import scalanative.unsigned._;
import input.Input
import math.Vector2
import math.Vector3
import transform.{Transform,RawTransform}
import transform.getPosition
import transform.setPosition
import ui.core.FreeLayoutItem

class Thumb extends Control derives ReflectType {
    var OnBeginDragCall:Option[(Vector2) => Unit] = None;
    var OnDragCall:Option[(Vector2) => Unit] = None;
    var OnEndDragCall:Option[(Vector2) => Unit] = None;

    var curPos:Vector3 = Vector3.zero;
    override def OnEnter(): Unit = {
        val thisEntity = this.createBaseEntity(true);
        EventManager.register(thisEntity,EventType.ALL_DRAG,this.OnElementEvent);
        this.loadControlTemplate();
    }

    protected def OnElementEvent(typ:UInt,args:Any):Unit = {
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
    }

    protected def OnDrag():Unit = {
        val delta = Input.getMoveDelta();
        this.OnDragCall.foreach(_(delta))
    }

    protected def OnEndDrag():Unit = {
      this.OnEndDragCall.foreach(_(Input.getMousePos()))
    }

    override def Exit(): Unit = {
        EventManager.unRegister(this.entity.get);
        super.Exit();
    }
}
