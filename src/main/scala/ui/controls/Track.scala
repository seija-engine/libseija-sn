package ui.controls
import core.reflect.*
import math.Vector2
import transform.Transform
import math.Vector3
import core.Entity
import ui.core.Rect2D
import ui.core.FreeLayout
import ui.core.FreeLayoutItem
import core.UpdateMgr
import ui.{LayoutUtils, UIModule}
import ui.core.Orientation
import ui.core.SizeValue

import scala.Float
import ui.core.Canvas

class Track extends RangeBase derives ReflectType {
  protected var _orientation: Orientation = Orientation.Horizontal
  protected var _trackLength: Float = Float.NaN
  protected var _thumbSize: Float = Float.NaN
  protected var _fillLength:Float = 0
  private var _density: Float = Float.NaN


  var thumb: Thumb = Thumb()
  private var postDirty: Boolean = false

  //region Setter

  def orientation: Orientation = _orientation;
  def orientation_=(value: Orientation): Unit = {
    _orientation = value;
    this.callPropertyChanged("orientation", this);
  }
  def trackLength: Float = this._trackLength
  def trackLength_=(value: Float): Unit = {
    this._trackLength = value;
    callPropertyChanged("trackLength", this)
  }
  def thumbSize: Float = this._thumbSize
  def thumbSize_=(value: Float): Unit = {
    this._thumbSize = value
    callPropertyChanged("thumbSize", this)
  }
  def fillLength: Float = this._fillLength
  def fillLength_=(value:Float):Unit = { this._fillLength = value; callPropertyChanged("fillLength",this) }
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
    LayoutUtils.addPostLayout(this.postLayoutProcess)
  }

  def valueFormDistance(horValue:Float,verValue:Float):Float = {

    this._orientation match
      case Orientation.Horizontal => horValue / this._density
      case Orientation.Vertical => verValue / this._density
  }

  protected def postLayoutProcess(step: Int): Unit = {
    if (LayoutUtils.isDirty(this.getEntity().get, step)) {
      this.postDirty = true
    }
    if (this.postDirty) {
      this.postDirty = false
      this.setUIByValue()
      LayoutUtils.addPostLayoutDirtyEntity(this.thumb.getEntity().get)
    }
  }

  override def onPropertyChanged(propertyName: String): Unit = {
    super.onPropertyChanged(propertyName)
    val isThumbSize = propertyName == "thumbSize"
    if (isThumbSize) {
      this._orientation match
        case Orientation.Horizontal => this.thumb.width = SizeValue.Pixel(this.thumbSize)
        case Orientation.Vertical => this.thumb.height = SizeValue.Pixel(this.thumbSize)
    }

    if (this._trackLength.isNaN) { this.postDirty = true; return }
    val hasDirty = propertyName == "minValue" || propertyName == "maxValue" || propertyName == "value" || isThumbSize;
    if(hasDirty) {
      this._value = this.clipValue(this._value)
      this.setUIByValue()
      if(LayoutUtils.postLayoutStep >= 0) {
        LayoutUtils.addPostLayoutDirtyEntity(this.thumb.getEntity().get)
      }
    }
  }

  protected def setUIByValue(): Unit = {
    val thumbRect = this.thumb.getEntity().get.get[Rect2D]()
    val thisRect = this.getEntity().get.get[Rect2D]()
    this.updateTrackAndDensity(thisRect._1, thisRect._2, thumbRect._1, thumbRect._2)

    val halfThumbX: Float = thumbRect._1 * 0.5f
    val halfThumbY: Float = thumbRect._2 * 0.5f
    val posValue = this._value * this._density
    val freeItem = this.thumb.getEntity().get.get[FreeLayoutItem]()

    this._orientation match
      case Orientation.Horizontal => {
        freeItem._1 = posValue
        this.fillLength = posValue + halfThumbX
      }
      case Orientation.Vertical => {
        freeItem._2 = posValue
        this.fillLength = posValue + halfThumbY
      }
  }

  protected def updateTrackAndDensity(rectW:Float,rectH:Float,thumbW:Float,thumbH:Float):Unit = {
    this._orientation match {
      case Orientation.Horizontal => this.trackLength = rectW - thumbW
      case Orientation.Vertical => this.trackLength = rectH - thumbH
    }
    this._density = this.trackLength / (this._maxValue - this._minValue)
  }

  override def Exit(): Unit = {
    LayoutUtils.removePostLayout(this.postLayoutProcess)
    super.Exit()
  }
}