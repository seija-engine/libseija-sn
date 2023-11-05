package ui.controls
import core.reflect.ReflectType
import ui.controls.Selector.SelectedEvent
import ui.event.RouteEvent
import ui.event.RouteEventArgs
import ui.controls.Selector.ItemInfo
import scala.collection.mutable.ArrayBuffer
import ui.controls.Selector.GetIsSelected
import ui.controls.Selector.IsSelectedProperty

class Selector extends ItemsControl derives ReflectType {
  var _selectIndex: Int = -1
  def selectIndex: Int = this._selectIndex
  def selectIndex_=(value: Int): Unit = {
    this._selectIndex = value; callPropertyChanged("selectIndex", this)
  }

  def isSelected: Boolean = this.GetPropValue(Selector.IsSelectedProperty).asInstanceOf[Boolean]
  def isSelected_=(value: Boolean): Unit = {
    this.SetPropValue(Selector.IsSelectedProperty,value); callPropertyChanged("isSelected", this)
  }

  private var _selectedItems: InternalSelectedItemsStorage = new InternalSelectedItemsStorage()

  override def Enter(): Unit = {
    super.Enter()
    this.routeEventController.addEvent(Selector.SelectedEvent, onSelectedEvent)
    this.routeEventController.addEvent(Selector.UnselectedEvent, onUnselectedEvent)
  }

  def onSelectedEvent(args: RouteEventArgs): Unit = {
    val event = args.asInstanceOf[SelectEventArgs]

    this.NotifyIsSelectedChanged(event.source, true, args)
  }

  def onUnselectedEvent(args: RouteEventArgs): Unit = {
    val event = args.asInstanceOf[UnselectEventArgs]
    this.NotifyIsSelectedChanged(event.source, false, args)
  }

  def NotifyIsSelectedChanged(element: UIElement,selected: Boolean,e: RouteEventArgs): Unit = {
    e.handled = true
    if(this.SelectionChange.active) return
    println(s"NotifyIsSelectedChanged:${selected} ${element}")
    val item = GetItemOrContainerFromContainer(element);
    if (item != null) {
      this.SetSelectedHelper(item, element, selected)
    }
  }

  private case class SelectionChanger(selector: Selector) {
    var toSelect: ArrayBuffer[ItemInfo] = ArrayBuffer.empty
    var toUnselect: ArrayBuffer[ItemInfo] = ArrayBuffer.empty
    var active:Boolean = false;
    def Begin(): Unit = {
      this.active = true;
      this.toSelect.clear()
      this.toUnselect.clear()
    }

    def Select(info: ItemInfo): Boolean = {
      val idx = this.toUnselect.indexOf(info);
      if (idx >= 0) {
        this.toUnselect.remove(idx)
        return true;
      }
      if(this.selector._selectedItems.Contains(info)) return false;
      if (this.toSelect.contains(info)) return true;
      if (this.toSelect.length > 0) {

        this.toSelect.foreach { itemInfo =>
          this.selector.ItemSetIsSelected(itemInfo, false)
        };
        this.toSelect.clear()
      }
      this.toSelect += info;
      true
    }

    def Unselect(info: ItemInfo): Boolean = {
      val idx = this.toSelect.indexOf(info);
      if (idx >= 0) {
        this.toSelect.remove(idx)
        return true;
      }
      if(!this.selector._selectedItems.Contains(info)) return false;
      if(this.toUnselect.contains(info)) return false;
      this.toUnselect += info;
      true
    }

    def End(): Unit = {
      this.active = false;
    }
  }

  private val SelectionChange: SelectionChanger = SelectionChanger(this)

  def SetSelectedHelper(
      itemData: Any,
      ui: UIElement,
      selected: Boolean
  ): Unit = {
    this.SelectionChange.Begin()
    val info = Selector.ItemInfo(itemData, Some(ui))
    info.Update(this.itemGenerator)
    if (selected) {
      this.SelectionChange.Select(info)
    } else {
      this.SelectionChange.Unselect(info)
    }
    this.SelectionChange.End()
  }

  def ItemSetIsSelected(info: ItemInfo, value: Boolean) = {
    if (info.container.isDefined) {
      if(GetIsSelected(info.container.get) != value) {
         info.container.get.SetPropValue(IsSelectedProperty,value)
      }
    } else if(info.item.isInstanceOf[UIElement]) {
      info.item.asInstanceOf[UIElement].SetPropValue(IsSelectedProperty,value)
    }
  }

  override def Exit(): Unit = {
    super.Exit()
    this.routeEventController.removeEvent(Selector.SelectedEvent)
    this.routeEventController.removeEvent(Selector.UnselectedEvent)
  }
}

object Selector {
  val IsSelectedProperty:PropertyDefine = PropertyDefine("Selector.IsSelected",false)

  val SelectedEvent: RouteEvent = RouteEvent("SelectedEvent", classOf[Selector])
  val UnselectedEvent: RouteEvent =
    RouteEvent("UnselectedEvent", classOf[Selector])

  case class ItemInfo(item: Any, var container: Option[UIElement]) {
    var index: Int = -1
    def Update(generator: ItemContainerGenerator): Unit = {
      if (this.index < 0 && this.container.isDefined) {
        this.index = generator.IndexFromItemData(this.container.get)
      }
    }

    override def equals(x: Any): Boolean = {
      x match
        case other: ItemInfo => {
          if (other.container.isDefined && this.container.isDefined) {
            return other.container == this.container;
          } else {
            other.item == this.item
          }
        }
        case _ => false
    }

  }

  def GetIsSelected(elem:UIElement):Boolean = elem.GetPropValue(IsSelectedProperty).asInstanceOf[Boolean]

  def SetIsSelected(elem:UIElement,value:Boolean) = elem.SetPropValue(IsSelectedProperty,value)
}

class SelectEventArgs(val source: UIElement)
    extends RouteEventArgs(Selector.SelectedEvent, false)
class UnselectEventArgs(val source: UIElement)
    extends RouteEventArgs(Selector.UnselectedEvent, false)

class InternalSelectedItemsStorage {
  private var _list: ArrayBuffer[ItemInfo] = ArrayBuffer.empty

  def Contains(e: ItemInfo): Boolean = {
    this._list.contains(e)
  }

  def Add(info: ItemInfo): Unit = {
    this._list += info;
  }

  def Remove(info: ItemInfo): Boolean = {
    val idx = this._list.indexOf(info)
    if (idx >= 0) {
      this._list.remove(idx)
      true
    } else { false }
  }

}