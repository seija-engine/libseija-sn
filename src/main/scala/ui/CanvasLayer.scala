package ui
import _root_.core.Entity
import math.Vector3
import transform.Transform
import ui.controls.UIElement

class CanvasLayer(cameraEntity:Entity,layerZ:Float,isPopup:Boolean) extends UIElement {
  override def OnEnter(): Unit = {
    val thisEntity = Entity.spawnEmpty()
      .add[ui.core.Rect2D]()
      .add[Transform](t => {
        t.parent = Some(cameraEntity)
        t.position = Vector3(0, 0, layerZ)
      }).add[ui.core.Canvas]()
    if(!isPopup) {
      thisEntity.add[ui.core.ItemLayout]()
    } else {
      thisEntity.add[ui.core.FreeLayout]()

    }
    this.entity = Some(thisEntity)
  }

  def addElement(element: UIElement):Unit = {
    this.addChild(element)
    element.Enter()
  }
}
