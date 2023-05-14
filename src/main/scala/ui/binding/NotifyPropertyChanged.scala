package ui.binding
import scala.collection.mutable.HashMap
import scala.annotation.{MacroAnnotation, experimental};
import scala.quoted.*

type PropertyChangedCallBack = (INotifyPropertyChanged, String, Any, Any) => Unit;

trait INotifyPropertyChanged {
  var handles: HashMap[PropertyChangedCallBack, Any] = HashMap()

  def addPropertyChangedHandler(
      handler: PropertyChangedCallBack,
      param: Any
  ): Unit = {
    this.handles.put(handler, param)
  }

  def removePropertyChangedHandler(handler: PropertyChangedCallBack): Unit = {
    this.handles.remove(handler)
  }

  def callPropertyChanged(propertyName: String, sourceObj: Any): Unit = {
    this.onPropertyChanged(propertyName)
    for (kv <- this.handles) {
      kv._1.apply(this, propertyName, sourceObj, kv._2)
    }
  }

  def onPropertyChanged(propertyName: String): Unit = {}
}

trait BProp[T] {
  var value: T;
  val parent:INotifyPropertyChanged;
  def set(v: T): Unit;
}

inline def autoProp[T](p:INotifyPropertyChanged,fieldName: String, default: T) = ${autoPropImpl[T]('p,'fieldName, 'default)}

def autoPropImpl[T](base: Expr[INotifyPropertyChanged],fieldName: Expr[String], default: Expr[T])
                   (using Type[T])(using Quotes): Expr[BProp[T]] = {
  '{
    var newProp = new BProp[T] {
      var value: T = $default;
      val parent: INotifyPropertyChanged = ${base};
      def set(setValue: T) = {
        this.value = setValue;
        this.parent.callPropertyChanged($fieldName, this.parent)
      }
    }
    newProp
  }
}
