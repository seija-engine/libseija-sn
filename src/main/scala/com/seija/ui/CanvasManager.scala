package com.seija.ui
import java.util.ArrayList
import com.seija.core.Entity
import com.seija.transform.given
import com.seija.transform.Transform
import com.seija.ui.core.{Canvas, ItemLayout, Rect2D, UISystem, given}
import com.seija.render.{Camera, given}
import com.seija.math.Vector3
import com.seija.ui.controls.UIElement

import scala.collection.mutable.ArrayBuffer
import com.seija.core.Entity

object CanvasManager {
  protected var cameraEntity:Option[Entity] = None
  protected var rootEntity:Option[Entity] = None
  protected var canvasList:ArrayBuffer[CanvasLayer] = ArrayBuffer.empty

  def init():Unit = {
    this.cameraEntity = Some(Entity.spawnEmpty()
      .add[Transform]()
      .add[Camera](c => c.sortType = 1)
      .add[UISystem]()
      .add[com.seija.ui.core.UICanvas]())

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