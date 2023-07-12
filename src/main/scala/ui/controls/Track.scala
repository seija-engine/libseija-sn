package ui.controls
import core.reflect.*
import math.Vector2
import transform.Transform
import transform.setPosition
import math.Vector3
import core.Entity
import ui.core.Rect2D
import ui.core.FreeLayout
import ui.core.FreeLayoutItem
import core.UpdateMgr
import ui.UIModule
import ui.core.Orientation
import ui.core.SizeValue

import scala.Float
import ui.core.Canvas

class Track extends RangeBase derives ReflectType {
  protected var _orientation: Orientation = Orientation.Horizontal
  protected var _trackLength:Float = Float.NaN
  protected var _thumbSize:Float = Float.NaN
  private var _density:Float = 0
  var thumb: Thumb = Thumb()

  //region Setter

  def orientation: Orientation = _orientation;
  def orientation_=(value: Orientation): Unit = {
    _orientation = value; this.callPropertyChanged("orientation", this);
  }
  def trackLength: Float = this._trackLength

  def thumbSize:Float = this._thumbSize;
  def thumbSize_=(value:Float): Unit = {
    this._thumbSize = value;callPropertyChanged("thumbSize",this)
  }
  //endregion

  private def createEntity(): Entity = {
    val parentEntity = this.parent.flatMap(_.getEntity());
    val newEntity = Entity.spawnEmpty().add[Transform](_.parent = parentEntity).add[Rect2D]().add[Canvas]()
      .add[FreeLayout](v => {
        v.common.hor = this._hor;
        v.common.ver = this._ver;
        v.common.uiSize.width = this._width;
        v.common.uiSize.height = this._height;
        v.common.padding = this._padding;
        v.common.margin = this._margin;
      });
    this.entity = Some(newEntity);
    newEntity
  }

  override def OnEnter(): Unit = {
    this.createEntity()
    this.loadControlTemplate()
    this.thumb = this.thumb.clone()
    this.addChild(this.thumb)
  }

  override def onPropertyChanged(propertyName: String): Unit = {
    super.onPropertyChanged(propertyName)
    if(propertyName == "minValue" || propertyName == "maxValue" || propertyName == "value" || propertyName == "trackLength") {
      this.updateUIByValue()
    }
  }

  protected def updateUIByValue():Unit = {
    val rate = this._value / (this._maxValue - this.minValue)
    val realPos = rate * (this._trackLength - this._thumbSize)
  }
  /*
  private var cacheSize: Vector2 = Vector2.zero.clone()

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
    UIModule.addPostLayoutCall(this.entity.get,this.OnPostLayout);
    this.cacheSize.x = this.width.getPixel().getOrElse(0);
    this.cacheSize.y = this.height.getPixel().getOrElse(0);
  }

  private def createEntity(): Unit = {
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



  protected def OnPostLayout():Unit = {
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
    val halfThumbX: Float = thumbRect._1 * 0.5f;
    val halfThumbY: Float = thumbRect._2 * 0.5f;
    this._orientation match {
          case Orientation.Horizontal => {
            val thisWidth = thisRect._1
            val maxPos = thisWidth - thumbRect._1
            freeItem._1 = maxPos * this._value
            this.fillSize = thisWidth * this._value + halfThumbX
          }
          case Orientation.Vertical => {
            val thisHeight = thisRect._2
            val maxPos = thisRect._2 - thumbRect._2
            freeItem._2 = maxPos * this._value
            this.fillSize = thisHeight * this._value + halfThumbY
          }
    }
  }

  protected def onLayoutResize(): Unit = {
    this.updateThumbSize()
    this.updatePosByValue()
  }

  override def onPropertyChanged(propertyName: String): Unit = {
    propertyName match
      case "viewportSize" => this.updateThumbSize()
      case "maximum" => this.updateThumbSize()
      case _ =>
  }

  private def updateThumbSize():Unit = {
    if(this._viewportSize.isNaN || this.cacheSize.x == 0 || this.cacheSize.y == 0) return;
    val endSize = this._viewportSize / this._maximum * this.cacheSize.y;
    this.thumb.height = SizeValue.Pixel(endSize);
    this.updatePosByValue()
  }

  override def Exit(): Unit = {
    UIModule.removePostLayoutCall(this.entity.get)
    super.Exit()
  }*/
}
