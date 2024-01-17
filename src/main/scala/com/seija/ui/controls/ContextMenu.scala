package com.seija.ui.controls
import com.seija.core.reflect.ReflectType
import com.seija.core.UpdateMgr
import com.seija.input.Input
import com.seija.input.MouseButton
import com.seija.core.Time

class ContextMenu extends Menu derives ReflectType {
    var _isOpen:Boolean = false
    def isOpen:Boolean = this._isOpen
    def isOpen_=(value:Boolean):Unit = {
        this._isOpen = value;callPropertyChanged("isOpen")
    }
    
    private var isFirstOpen:Boolean = true;
    private var popup:Popup = new Popup(); 
    override def OnEnter(): Unit = {
         this.popup.mode = PlacementMode.Mouse;
         this.popup.Enter();
         super.OnEnter();
    }

    def Open():Unit = {
        this.popup.isOpen = true;
        if(isFirstOpen) {
            this.popup.addChild(this)
            this.getEntity().get.setParent(this.popup.getEntity())
            isFirstOpen = false;
        }
        this.popup.UpdatePos();
    }

    override def onUpdate(dt: Float): Unit = {
      if(this.popup.isOpen && (Input.getMouseUp(MouseButton.Left) || Input.getMouseUp(MouseButton.Right))) {
        this.waitCloseFrame = Time.Frame + 1
        
      }
      if(this.waitCloseFrame == Time.Frame) {
        this.closeALLMenu()
      }
    }

    override def closeALLMenu(): Unit = {
        super.closeALLMenu();
        this.popup.isOpen = false;
    }

}