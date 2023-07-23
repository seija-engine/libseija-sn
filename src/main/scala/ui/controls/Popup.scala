package ui.controls
import ui.controls.UIElement
import core.reflect.{Into, ReflectType}
import ui.{ContentProperty, LayoutUtils}
import core.{Entity, UpdateMgr}
import _root_.core.Time
import transform.{RawTransform, Transform, getWorldPosition}
import ui.core.{FreeLayoutItem, ItemLayout, LayoutAlignment, Rect2D}

import scala.scalanative.unsigned.ULong

enum PlacementMode(v:Int) {
  case Absolute extends PlacementMode(0)
  case Center extends PlacementMode(1)
  case Left extends PlacementMode(2)
  case Right extends PlacementMode(3)
  case Top extends PlacementMode(4)
  case Bottom extends PlacementMode(5)
  case Mouse extends PlacementMode(6)
}

object PlacementMode {
  given Into[String,PlacementMode] with {
    override def into(fromValue: String): PlacementMode = fromValue match {
      case "Absolute" => PlacementMode.Absolute
      case "Center" => PlacementMode.Center
      case "Left" => PlacementMode.Left
      case "Right" => PlacementMode.Right
      case "Top" => PlacementMode.Top
      case "Bottom" => PlacementMode.Bottom
      case "Mouse" => PlacementMode.Mouse
      case _ => PlacementMode.Bottom
    }
  }
}

@ContentProperty("child")
class Popup extends UIElement derives ReflectType {
  var _child:UIElement = UIElement.zero
  var _isOpen:Boolean = false
  var _mode:PlacementMode = PlacementMode.Bottom
  var _target:UIElement = null


  private var waitSetFrame:Option[ULong] = None
  //region Setter

  def isOpen: Boolean = this._isOpen
  def child:UIElement = this._child
  def mode:PlacementMode = PlacementMode.Bottom
  def target:UIElement = this._target
  def child_=(value:UIElement):Unit = {
    this._child = value; callPropertyChanged("child",this)
  }
  def isOpen_=(value:Boolean):Unit = {
    this._isOpen = value;callPropertyChanged("isOpen",this)
  }
  def mode_=(value:PlacementMode):Unit = {
    this._mode = value; callPropertyChanged("mode",this)
  }
  def target_=(value:UIElement):Unit = {
    this._target = value;callPropertyChanged("target",this)
  }
  //endregion

  override def OnEnter(): Unit = {
    UpdateMgr.add(this.OnUpdate)
    val topEntity = ui.CanvasManager.popup().getEntity().get
    val newEntity = Entity.spawnEmpty()
                          .add[Transform](_.parent = Some(topEntity) )
                          .add[Rect2D]()
    this.addEntityStateInfo(newEntity)
    newEntity.add[ItemLayout](v => {
                v.common.hor = LayoutAlignment.Center
                v.common.ver = LayoutAlignment.Center
                v.common.uiSize.width = this._width
                v.common.uiSize.height = this._height
                v.common.padding = this._padding
                v.common.margin = this._margin
            })
    newEntity.add[FreeLayoutItem]()
    this.entity = Some(newEntity)
    this.addChild(this.cloneChild())
    if(this._isOpen) {
      this.waitSetFrame = Some(Time.getFrameCount())
    }
  }

  protected def OnUpdate(dt:Float):Unit = {
    val curFrame = Time.getFrameCount()

    if(this.waitSetFrame.isDefined && curFrame > this.waitSetFrame.get ) {

      val targetElement:Option[UIElement] =  Option(this._target).orElse(this.parent)
      targetElement.foreach {element =>
        val targetEntity = element.getEntity().get
        val thisEntity = this.getEntity().get
        val thisRect = thisEntity.get[Rect2D]()
        val thisWidth = thisRect._1
        val thisHeight = thisRect._2
        println(s"this size:${thisWidth},${thisHeight}")
        val parentT:RawTransform = targetEntity.get[Transform]()
        val parentPos = parentT.getWorldPosition
        println(s"parent pos:${parentPos}")
      }
      this.waitSetFrame = None
    }
  }

  protected def cloneChild():UIElement = this._child.clone()

  override def Exit(): Unit = {
    super.Exit()
    UpdateMgr.remove(this.OnUpdate)
  }
} 