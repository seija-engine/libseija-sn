package ui
import java.util.ArrayList
import _root_.core.Entity
import transform.given;
import transform.Transform
import ui.core.given;
import ui.core.UISystem
import render.{Camera, given}
import math.Vector3
import ui.BaseControl;
import ui.controls2.UIElement

case class UICanvas(val cameraEntity: Entity,val rootEntity:Entity) extends UIElement {
  this.entity = Some(rootEntity);
  
  def addElement(uiElement: UIElement): Unit = {
    this.addChild(uiElement)
    uiElement.Enter();
  }
}

object UICanvas {
  var rootCanvasList: ArrayList[UICanvas] = new ArrayList[UICanvas]();
  def apply(): UICanvas = {
    val cameraEntity = Entity
      .spawnEmpty()
      .add[Transform]()
      .add[Camera](c => c.sortType = 1)
      .add[UISystem]()
      .add[ui.core.UICanvas]()
    val rootEntity = Entity
      .spawnEmpty()
      .add[ui.core.Rect2D]()
      .add[Transform](t => {t.parent = Some(cameraEntity);t.position = Vector3(0,0,-2) } )
      .add[ui.core.Canvas]()
      .add[ui.core.ItemLayout]()
    UICanvas(cameraEntity,rootEntity)
  }
  
  def create(): UICanvas = {
    var newCanvas: UICanvas = UICanvas();
    rootCanvasList.add(newCanvas);
    newCanvas
  }

  def fst():UICanvas = rootCanvasList.get(0)
}
