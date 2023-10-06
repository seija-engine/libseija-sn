package ui.controls
import core.reflect.ReflectType
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
        this.routeEventController.addEvent(Selector.SelectedEvent,onSelectdEvent)
        this.routeEventController.addEvent(Selector.UnselectedEvent,onUnselectdEvent)
    }

    def onSelectdEvent(args:RouteEventArgs):Unit = {
        args.handled = true
    }

    def onUnselectdEvent(args:RouteEventArgs):Unit = {
        args.handled = true
    }
}

object Selector {
  val SelectedEvent:RouteEvent = RouteEvent("SelectedEvent",classOf[Selector])
  val UnselectedEvent:RouteEvent = RouteEvent("UnselectedEvent",classOf[Selector])
}