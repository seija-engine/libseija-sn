package ui.controls
import core.reflect.*;
import math.Vector2
import transform.Transform
import transform.setPosition
import math.Vector3
import core.Entity
import ui.core.Rect2D
import ui.core.FreeLayout
import ui.core.FreeLayoutItem
import core.UpdateMgr

class Track extends Control derives ReflectType {
    private var startBtn:RepeatButton = RepeatButton();
    private var endBtn:RepeatButton = RepeatButton();
    private var thumb:Thumb = Thumb();

    private var cacheSize:Vector2 = Vector2.zero.clone();
    private var curPosRate:Float = 0;
    override def Awake(): Unit = {
        super.Awake();
        this.thumb.OnBeginDragCall = Some(this.OnStartDrag);
        this.thumb.OnDragCall = Some(this.OnDrag);
        this.thumb.OnEndDragCall = Some(this.OnEndDrag);
    }
    override def Enter(): Unit = {
        super.Enter();
        this.children.foreach {child => 
            child.getEntity().get.add[FreeLayoutItem]();    
        };
    }

    override def OnEnter(): Unit = {
        this.createEntity();
        this.loadControlTemplate();
        this.addChild(this.thumb);
        UpdateMgr.add(this.OnUpdate);
        this.cacheSize.x = this.width.getPixel().getOrElse(0);
        this.cacheSize.y = this.height.getPixel().getOrElse(0);
    }

    protected def createEntity():Unit = {
        val parentEntity = this.parent.flatMap(_.getEntity());
        val newEntity = Entity.spawnEmpty()
                              .add[Transform](_.parent = parentEntity)
                              .add[Rect2D]()
                              .add[FreeLayout](v => {
                                v.common.hor = this._hor;
                                v.common.ver = this._ver;
                                v.common.uiSize.width = this._width;
                                v.common.uiSize.height = this._height;
                                v.common.padding = this._padding;
                                v.common.margin = this._margin;
                              });
        this.entity = Some(newEntity);
    }

    protected def OnStartDrag(pos:Vector2):Unit = {
    }

    protected def OnDrag(delta:Vector2):Unit = {
        println(delta);
    }

    protected def OnEndDrag(pos:Vector2):Unit = {

    }

    protected def OnUpdate(dt:Float):Unit = {
       val rawRect = this.getEntity().get.get[Rect2D]();
       val x = rawRect._1;
       val y = rawRect._2;
       if(this.cacheSize.x != x && this.cacheSize.y != y) {
          this.cacheSize.x = x;
          this.cacheSize.y = y;
          this.onLayoutResize();
       }
    }

    protected def onLayoutResize():Unit = {
    }

    override def Exit(): Unit = {
        super.Exit();
        UpdateMgr.remove(this.OnUpdate);
    }
}
