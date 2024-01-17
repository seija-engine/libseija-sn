package com.seija.ui.controls
import com.seija.core.reflect.*
import com.seija.ui.core.Orientation
import scalanative.unsigned.*
import com.seija.ui.event.EventType
import com.seija.ui.visualState.ViewStates
import com.seija.ui.event.EventManager
import com.seija.core.UpdateMgr
import com.seija.input.Input
import com.seija.input.KeyCode
import com.seija.input.MouseButton
import com.seija.core.Time
import scala.collection.mutable.ArrayBuffer

class Menu extends ItemsControl derives ReflectType {
 
  protected  var waitCloseFrame:Long = 0
  override def OnEnter(): Unit = {
    super.OnEnter()
    UpdateMgr.add(this.onUpdate)
  }

  private var selectItem:Option[MenuItem] = None

  def onChildItemEvent(item:MenuItem, typ:UInt):Unit = {
    
    val zero = 0.toUInt
    val isMouseEnter = (typ & EventType.MOUSE_ENTER) != zero
    val isMouseLeave = (typ & EventType.MOUSE_LEAVE) != zero
    val isClick = (typ & EventType.CLICK) != zero
   
    
    if(isClick) {
      if(this.selectItem.isEmpty) {
        this.setSelectItem(item)
      }
    }                             
    
    this.selectItem match
      case None => {
        if(isMouseEnter) {
          item.setViewState(ViewStates.CommonStates,ViewStates.MouseOver)
        }
        if(isMouseLeave) {
          item.setViewState(ViewStates.CommonStates,ViewStates.Normal)
        }
      }
      case Some(value) => {
        if(isMouseEnter) {
          if(item != value) {
            value.setViewState(ViewStates.CommonStates,ViewStates.Normal)
            item.setViewState(ViewStates.CommonStates,ViewStates.MouseOver)
            this.setSelectItem(item)
          }
        }
      }
  }

  private var childItems:ArrayBuffer[MenuItem] = ArrayBuffer.empty

  def closeALLMenu():Unit = {
    val wrapPanel = itemsPresenter.map(_.children(0)).orNull
    if(wrapPanel != null) {
      for(child <- wrapPanel.children) {
        child match
          case item:MenuItem => item.closeALLItem()
          case _ =>
      }
    }
    this.selectItem = None
  }

  def setSelectItem(item:MenuItem):Unit = {
    this.selectItem.foreach {v => v.closeALLItem() }
    item.isSubmenuOpen = true
    this.selectItem = Some(item)
    item.setViewState(ViewStates.CommonStates,ViewStates.Pressed)
  }

  def onUpdate(dt:Float):Unit = {
    if(this.selectItem.isDefined ) {
      if(Input.getMouseUp(MouseButton.Left) || Input.getMouseUp(MouseButton.Right)) {
        this.waitCloseFrame = Time.Frame + 1
      }
    }
    if(this.waitCloseFrame == Time.Frame) {
      this.closeALLMenu()
    }
  }

  override def Exit(): Unit = {
    super.Exit()
    UpdateMgr.remove(this.onUpdate)
  }

}
