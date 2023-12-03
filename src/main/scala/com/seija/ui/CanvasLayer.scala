package com.seija.ui
import com.seija.core.Entity
import com.seija.math.Vector3
import com.seija.transform.Transform
import com.seija.ui.controls.UIElement
import com.seija.core.Entity

class CanvasLayer(cameraEntity:Entity,layerZ:Float,isPopup:Boolean) extends UIElement {
  override def OnEnter(): Unit = {
    val thisEntity = Entity.spawnEmpty()
      .add[com.seija.ui.core.Rect2D]()
      .add[Transform](t => {
        t.parent = Some(cameraEntity)
        t.position = Vector3(0, 0, layerZ)
      }).add[com.seija.ui.core.Canvas]()
    if(!isPopup) {
      thisEntity.add[com.seija.ui.core.ItemLayout]()
    } else {
      thisEntity.add[com.seija.ui.core.FreeLayout]()

    }
    this.entity = Some(thisEntity)
  }

  def addElement(element: UIElement):Unit = {
    this.addChild(element)
    element.Enter()
  }
}
