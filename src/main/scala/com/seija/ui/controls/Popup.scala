package com.seija.ui.controls
import com.seija.ui.controls.UIElement
import com.seija.core.reflect.{Into, ReflectType}
import com.seija.ui.{ContentProperty, LayoutUtils}
import com.seija.core.{Entity, UpdateMgr}
import com.seija.math.{Vector3, Vector2}
import com.seija.core.Time
import com.seija.transform.{RawTransform, getWorldPosition}
import com.seija.transform.{FFISeijaTransform, Transform, getWorldPosition}
import com.seija.ui.core.{FreeLayoutItem, ItemLayout, LayoutAlignment, Rect2D}
import scala.scalanative.unsigned._
import com.seija.core.Time
import com.seija.math
import com.seija

enum PlacementMode(v:Int) {
  case Center extends PlacementMode(0)
  case Left extends PlacementMode(1)
  case Right extends PlacementMode(2)
  case Top extends PlacementMode(3)
  case Bottom extends PlacementMode(4)
  case Mouse extends PlacementMode(5)
  case Absolute extends PlacementMode(6)
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


  private var waitSetFrame:Option[ULong] =  None
  private var _curIsShow:Boolean = false
  //region Setter

  def isOpen: Boolean = this._isOpen
  def child:UIElement = this._child
  def mode:PlacementMode = PlacementMode.Bottom
  def target:UIElement = this._target
  def child_=(value:UIElement):Unit = {
    this._child = value; callPropertyChanged("child")
  }
  def isOpen_=(value:Boolean):Unit = {
    this._isOpen = value;callPropertyChanged("isOpen")
  }
  def mode_=(value:PlacementMode):Unit = {
    this._mode = value; callPropertyChanged("mode")
  }
  def target_=(value:UIElement):Unit = {
    this._target = value;callPropertyChanged("target")
  }
  //endregion
  

  override def OnEnter(): Unit = {
    this._active = this._isOpen
    this._curIsShow = this._isOpen
    if(this._active) {
      this.waitSetFrame = Some(Time.getFrameCount())
    }
    LayoutUtils.addPostLayout(OnPostLayout)
    val topEntity = com.seija.ui.CanvasManager.popup().getEntity().get
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
               
  }

  override def OnAddContent(value:Any):Unit = {
    this.addChild(this.cloneChild()) 
  }

  override def onPropertyChanged(propertyName: String): Unit = {
    super.onPropertyChanged(propertyName)
    if(!this.isEntered) { return }
    if(propertyName == "isOpen" && this._isOpen != this._curIsShow) {
      this._curIsShow = this._isOpen
      this.active =  this._isOpen
      this.waitSetFrame = Some(Time.getFrameCount() + 1.toULong)
    }
  }

  protected def OnPostLayout(step:Int):Unit = {
    val curFrame = Time.getFrameCount()
    if(this.waitSetFrame.isDefined && curFrame == this.waitSetFrame.get) {
      val targetPos = this.calcPopupPos()
      val thisEntity = this.getEntity().get
      val freeItem = thisEntity.get[FreeLayoutItem]()
      freeItem._1 = targetPos.x.round
      freeItem._2 = targetPos.y.round
      LayoutUtils.addPostLayoutDirtyEntity(com.seija.ui.CanvasManager.popup().getEntity().get)
      this.waitSetFrame = None
    }
  }

  protected def calcPopupPos():Vector2 = {
    val thisEntity = this.getEntity().get
    val thisRect = thisEntity.get[Rect2D]().toData
    val (targetUIPos,targetRect) = this.calcTargetInfo()
    val ltPos:Vector2 = this._mode match {
      case PlacementMode.Center => {
        Vector2(targetUIPos.x - thisRect.width * 0.5f,
                targetUIPos.y - thisRect.height * 0.5f)
      }
      case PlacementMode.Left => {
        val xOffset = targetUIPos.x - (thisRect.width + targetRect.width * targetRect.anchorX)
        val yOffset =  targetUIPos.y - targetRect.top
        Vector2(xOffset,yOffset)
      }
      case PlacementMode.Right => {
        val xOffset = targetUIPos.x +  targetRect.width * targetRect.anchorX
        val yOffset =  targetUIPos.y - targetRect.top
        Vector2(xOffset, yOffset)
      }
      case PlacementMode.Top => {
        val xOffset = targetUIPos.x - targetRect.width * targetRect.anchorX
        val yOffset =  targetUIPos.y - targetRect.height * 0.5f - thisRect.height
        Vector2(xOffset, yOffset)
      }
      case PlacementMode.Bottom => {
        val xOffset = targetUIPos.x - targetRect.width * targetRect.anchorX
        val yOffset =  targetUIPos.y + targetRect.height * 0.5f
        Vector2(xOffset, yOffset)
      }
      case PlacementMode.Absolute | PlacementMode.Mouse => ???
    }
    ltPos
  }

  private def calcTargetInfo():(Vector3,math.Rect2D) = {
    if(isHasTargetMode) {
      val targetElement: Option[UIElement] = Option(this._target).orElse(this.parent)
      if(targetElement.isEmpty) {
        slog.error("popup need target")
        return (Vector3.zero,com.seija.math.Rect2D.zero)
      }
      val targetEntity = targetElement.get.getEntity().get
      
      val mat4 = Transform.relativeTo(targetEntity,Some(com.seija.ui.CanvasManager.fst().getEntity().get))
      
      val targetPos = com.seija.ui.core.FFISeijaUI.toUIPos(mat4.pos)
      
      val targetRect = targetEntity.get[Rect2D]()
      //println(targetRect.toData)
      return (targetPos,targetRect.toData)
    }
    ???
  }

  private def isHasTargetMode:Boolean = {
    this._mode match
      case PlacementMode.Center |
           PlacementMode.Left   |
           PlacementMode.Right  |
           PlacementMode.Top    |
           PlacementMode.Bottom => true
      case _ => false
  }

  protected def cloneChild():UIElement = this._child.clone()

  override def Exit(): Unit = {
    super.Exit()
    LayoutUtils.removePostLayout(OnPostLayout)
  }
} 