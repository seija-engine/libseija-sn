package ui.event
import ui.controls.ScrollBar
import ui.controls.UIElement
import scala.collection.mutable

type RouteEventHandle = (RouteEventArgs) => Unit;

case class RouteEvent(
  val eventName:String,
  val ownerType:Class[_]
);

case class RouteEventArgs(
  val event:RouteEvent,
  var handled:Boolean = false
);

trait IRouteEventElement {
   def routeEventController:RouteEventController
   def getRouteEventParent:Option[IRouteEventElement]
}


case class RouteEventController(elem:IRouteEventElement) {
  private val routeEventDict: mutable.HashMap[RouteEvent, RouteEventHandle] = mutable.HashMap.empty
  def addEvent(routeEvent: RouteEvent, callFn: RouteEventHandle): Unit = {
    this.routeEventDict.put(routeEvent, callFn)
  }
  def removeEvent(routeEvent: RouteEvent): Unit = {
    this.routeEventDict.remove(routeEvent)
  }

  def fireEvent(eventArgs:RouteEventArgs):Unit = {
      var curElement = elem.getRouteEventParent

      while(curElement.isDefined) {
        val curController = curElement.get.routeEventController
        val evHandle = curController.routeEventDict.get(eventArgs.event)
        curElement = curElement.get.getRouteEventParent
        evHandle.foreach {handle =>
          handle.apply(eventArgs)
          if(eventArgs.handled) {
            curElement = None
          }
        }
      }
  }
}