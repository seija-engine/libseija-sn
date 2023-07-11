package ui.event
import ui.controls.ScrollBar

case class RouteEvent();
case class RouteEventArgs();

object UIRouteEventManager {
  def registerClassHandler(forType:Class[_],event:RouteEvent,handleFunc:(args:RouteEventArgs) => Unit):Unit = {

  }

  def registerEvent(eventName:String, handlerType:Class[_], ownerType:Class[_]):RouteEvent = {
    RouteEvent()
  }


  def test():Unit = {

  }
}

