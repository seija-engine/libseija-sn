package ui
import scala.collection.mutable.ListBuffer

trait INotifyPropertyChanged {
  var propertyChangedHandlers: ListBuffer[(INotifyPropertyChanged, PropertyChangedEventArgs) => Unit] = ListBuffer()

  def addPropertyChangedHandler(handler: (INotifyPropertyChanged, PropertyChangedEventArgs) => Unit): Unit = {
    propertyChangedHandlers += handler
  }

  def removePropertyChangedHandler(handler: (INotifyPropertyChanged, PropertyChangedEventArgs) => Unit): Unit = {
    propertyChangedHandlers -= handler
  }

  def callPropertyChanged(propertyName: String): Unit = {
    propertyChangedHandlers.foreach(_.apply(this, new PropertyChangedEventArgs(propertyName)))
  }

  def onPropertyChanged(propertyName:String):Unit = {}
}

class PropertyChangedEventArgs(val propertyName: String)