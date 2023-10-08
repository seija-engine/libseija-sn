package ui.controls
import core.reflect.ReflectType
import ui.controls.Selector.SelectedEvent
import ui.event.RouteEvent
import ui.event.RouteEventArgs

class Selector extends ItemsControl derives ReflectType {
    var _selectIndex:Int = -1
    def selectIndex:Int = this._selectIndex
    def selectIndex_=(value:Int):Unit = {
        this._selectIndex = value;callPropertyChanged("selectIndex",this)
    }

    var _isSelected:Boolean = false
    def isSelected:Boolean = this._isSelected
    def isSelected_=(value:Boolean):Unit = {
        this._isSelected = value;callPropertyChanged("isSelected",this)
    }

    override def Enter(): Unit = {
        super.Enter()
        this.routeEventController.addEvent(Selector.SelectedEvent,onSelectedEvent)
        this.routeEventController.addEvent(Selector.UnselectedEvent,onUnselectedEvent)
    }

    def onSelectedEvent(args:RouteEventArgs):Unit = {
       this.NotifyIsSelectedChanged(true,args)
    }

    def onUnselectedEvent(args:RouteEventArgs):Unit = {
      this.NotifyIsSelectedChanged(false,args)
    }

    def NotifyIsSelectedChanged(selected:Boolean,e:RouteEventArgs):Unit = {
      println(s"NotifyIsSelectedChanged:${selected}")
    }

    override def Exit():Unit = {
      super.Exit()
      this.routeEventController.removeEvent(Selector.SelectedEvent)
      this.routeEventController.removeEvent(Selector.UnselectedEvent)
    }
}

object Selector {
  val SelectedEvent:RouteEvent = RouteEvent("SelectedEvent",classOf[Selector])
  val UnselectedEvent:RouteEvent = RouteEvent("UnselectedEvent",classOf[Selector])
}