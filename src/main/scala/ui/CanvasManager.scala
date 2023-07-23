package ui
import java.util.ArrayList
import _root_.core.Entity
import transform.given
import transform.Transform
import ui.core.{Canvas, ItemLayout, Rect2D, UISystem, given}
import render.{Camera, given}
import math.Vector3
import ui.controls.UIElement

import scala.collection.mutable.ArrayBuffer

object CanvasManager {
  protected var cameraEntity:Option[Entity] = None
  protected var rootEntity:Option[Entity] = None
  protected var canvasList:ArrayBuffer[CanvasLayer] = ArrayBuffer.empty

  def init():Unit = {
    this.cameraEntity = Some(Entity.spawnEmpty()
      .add[Transform]()
      .add[Camera](c => c.sortType = 1)
      .add[UISystem]()
      .add[ui.core.UICanvas]())

    this.rootEntity = Some(Entity.spawnEmpty()
      .add[Transform](t => {
        t.parent = Some(this.cameraEntity.get)
        t.position = Vector3(0, 0, -2)
      })
      .add[Rect2D]().add[Canvas]().add[ItemLayout]())


    val bgLayer = CanvasLayer(this.rootEntity.get,0,false)
    this.canvasList += bgLayer
    val popupLayer = CanvasLayer(this.rootEntity.get, 0,true)
    this.canvasList += popupLayer

    this.canvasList.foreach(_.Enter())
  }

  def fst():CanvasLayer = this.canvasList(0)

  def popup():CanvasLayer = this.canvasList(1)
}