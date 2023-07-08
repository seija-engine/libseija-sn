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
import ui.core.Orientation
import ui.core.SizeValue;
import scala.Float;
import ui.core.Canvas

class Track extends Control derives ReflectType {
  var _orientation: Orientation = Orientation.Horizontal;
  var _value: Float = 0;
  protected var _fillSize:Float = 0;
  protected var _viewportSize:Float = Float.NaN;

  var thumb: Thumb = Thumb();

  private var cacheSize: Vector2 = Vector2.zero.clone();
  private var curPosRate: Float = 0;

  def orientation: Orientation = _orientation;
  def orientation_=(value: Orientation): Unit = {
    _orientation = value; this.callPropertyChanged("orientation", this);
  }
  def value: Float = this._value;
  def value_=(value: Float): Unit = {
    _value = value; this.callPropertyChanged("value", this)
    this.updatePosByValue();
  }
  def fillSize: Float = this._fillSize;
  def fillSize_=(value: Float): Unit = {
    _fillSize = value; this.callPropertyChanged("fillSize", this);
  }
  def viewportSize:Float = this._viewportSize;
  def viewportSize_=(value: Float): Unit = {
    _viewportSize = value;
    this.callPropertyChanged("viewportSize", this);
  }
  override def Awake(): Unit = {
    super.Awake();
  }
  override def Enter(): Unit = {
    super.Enter();
    this.thumb.getEntity().get.add[FreeLayoutItem]();
    this.thumb.OnBeginDragCall = Some(this.OnStartDrag);
    this.thumb.OnDragCall = Some(this.OnDrag);
    this.thumb.OnEndDragCall = Some(this.OnEndDrag);
  }

  override def OnEnter(): Unit = {
    this.createEntity();
    this.loadControlTemplate();
    this.thumb = this.thumb.clone();
    this.addChild(this.thumb);
    UpdateMgr.add(this.OnUpdate);
    this.cacheSize.x = this.width.getPixel().getOrElse(0);
    this.cacheSize.y = this.height.getPixel().getOrElse(0);
  }

  protected def createEntity(): Unit = {
    val parentEntity = this.parent.flatMap(_.getEntity());
    val newEntity = Entity
      .spawnEmpty()
      .add[Transform](_.parent = parentEntity)
      .add[Rect2D]()
      .add[Canvas]()
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

  protected def OnStartDrag(pos: Vector2): Unit = {}

  protected def OnDrag(delta: Vector2): Unit = {
    val freeItem = this.thumb.getEntity().get.get[FreeLayoutItem]();
    val thisRect = this.getEntity().get.get[Rect2D]();
    val thumbRect = this.thumb.getEntity().get.get[Rect2D]();
    val halfThumbX:Float = thumbRect._1 * 0.5f;
    val halfThumbY:Float = thumbRect._2 * 0.5f;
    this._orientation match {
      case Orientation.Horizontal => {
        val newX = freeItem._1 + delta.x;
        val maxPos = thisRect._1 - thumbRect._1;
        if (newX >= 0 && newX <= maxPos) {
          freeItem._1 = newX;
        }
        this._value = newX / maxPos;
        this.fillSize = halfThumbX + newX;
      }
      case Orientation.Vertical => {
        val newY = freeItem._2 + (-delta.y);
        val maxPos = thisRect._2 - thumbRect._2;
        if (newY >= 0 && newY <= maxPos) {
          freeItem._2 = newY;
        }
        this._value = newY / maxPos;
        this.fillSize = halfThumbY + newY;
      }
    }
    this.callPropertyChanged("value",this);
  }

  protected def OnEndDrag(pos: Vector2): Unit = {}

  protected def OnUpdate(dt: Float): Unit = {
    val rawRect = this.getEntity().get.get[Rect2D]();
    val x = rawRect._1;
    val y = rawRect._2;
    if (this.cacheSize.x != x || this.cacheSize.y != y) {
      this.cacheSize.x = x;
      this.cacheSize.y = y;
      this.onLayoutResize();
    }
  }

  protected def updatePosByValue(): Unit = {
    val freeItem = this.thumb.getEntity().get.get[FreeLayoutItem]();
    val thisRect = this.getEntity().get.get[Rect2D]();
    val thumbRect = this.thumb.getEntity().get.get[Rect2D]();
    this._orientation match {
      case Orientation.Horizontal => {
        val thisWidth = thisRect._1;
        val maxPos = thisWidth - thumbRect._1;
        freeItem._1 = maxPos * this._value;
        this.fillSize = thisWidth * this._value;
      }
      case Orientation.Vertical => {
        val thisHeight = thisRect._2;
        val maxPos = thisRect._2 - thumbRect._2;
        freeItem._2 = maxPos * this._value;
        this.fillSize = thisHeight * this._value;
      }
    }
  }

  protected def onLayoutResize(): Unit = {
    this.updatePosByValue();
    this.updateThumbSize();
  }

  protected def updateThumbSize():Unit = {
    if(this._viewportSize.isNaN) { return; }
  }

  override def Exit(): Unit = {
    super.Exit();
    UpdateMgr.remove(this.OnUpdate);
  }
}
