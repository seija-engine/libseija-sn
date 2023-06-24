package ui.controls
import core.reflect.*;
import math.Vector2
import transform.Transform
import transform.setPosition
import math.Vector3

class Track extends Control derives ReflectType {
    private var startBtn:RepeatButton = RepeatButton();
    private var endBtn:RepeatButton = RepeatButton();
    private var thumb:Thumb = Thumb();
    override def Awake(): Unit = {
        super.Awake();
        this.thumb.OnBeginDragCall = Some(this.OnStartDrag);
        this.thumb.OnDragCall = Some(this.OnDrag);
        this.thumb.OnEndDragCall = Some(this.OnEndDrag);
    }
    override def Enter(): Unit = {
        super.Enter();
        val thumbT = this.thumb.getEntity().get.get[Transform]();
        thumbT.setPosition(Vector3(-125,0,0));
    }

    override def OnEnter(): Unit = {
        super.OnEnter();
        this.addChild(this.thumb);
    }

    protected def OnStartDrag(pos:Vector2):Unit = {
        val thumbT = this.thumb.getEntity().get.get[Transform]();
        thumbT.setPosition(Vector3(-98,0,0));
    }

    protected def OnDrag(delta:Vector2):Unit = {
        println(delta);
    }

    protected def OnEndDrag(pos:Vector2):Unit = {

    }
}
