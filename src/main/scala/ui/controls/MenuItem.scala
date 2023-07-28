package ui.controls
import core.reflect.ReflectType
import ui.event.EventManager
import ui.event.EventType
import scalanative.unsigned._

class MenuItem extends HeaderedItemsControl derives ReflectType {
    protected var _isSubmenuOpen:Boolean = false

    def isSubmenuOpen:Boolean = this._isSubmenuOpen
    def isSubmenuOpen_=(value:Boolean):Unit = {
        this._isSubmenuOpen = value; callPropertyChanged("isSubmenuOpen",this)
    }

    override def OnEnter(): Unit = {
        super.OnEnter()
        EventManager.register(this.getEntity().get,EventType.CLICK,OnElementEvent)
    }

    protected def OnElementEvent(typ:UInt,px:Float,py:Float,args:Any):Unit = {
        val zero = 0.toUInt
        if((typ & EventType.CLICK) != zero) {
           if(!isSubmenuOpen) {
             isSubmenuOpen = true
           }
        }
    }

    override def Exit(): Unit = {
        EventManager.unRegister(this.getEntity().get)
        super.Exit()
    }
}