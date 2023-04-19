package ui.core
import scalanative.unsafe._
import core.RawComponentBuilder
import core.Entity
import core.RawComponent
import scala.scalanative.unsigned._
import ui.core.FFISeijaUI


type RawEventNode = CStruct5[UInt,UInt,Boolean,Boolean,Boolean];


class EventNode;

class EventNodeBuilder extends RawComponentBuilder {
  var eventType:UInt = 0.toUInt;
  var stopCapture:Boolean = false;
  var stopBubble:Boolean = false;
  var useCapture:Boolean = false;
  var userKey:String = null;

  override def build(entity: Entity): Unit = {
    val eventNodePtr = stackalloc[RawEventNode]();
    eventNodePtr._2 = this.eventType;
    eventNodePtr._3 = this.stopCapture;
    eventNodePtr._4 = this.stopBubble;
    eventNodePtr._5 = this.useCapture;
    FFISeijaUI.entityAddEventNode(core.App.worldPtr,entity.id,eventNodePtr,userKey)
  }
}

given EventNodeComponent:RawComponent[EventNode] with {
  type BuilderType = EventNodeBuilder;
  type RawType = Ptr[Byte];

  override def builder(): BuilderType = new EventNodeBuilder();

  override def getRaw(entity: Entity): RawType = ???
}


object EventNode {
    final val NONE = 0.toUInt;
    final val TOUCH_START = 1.toUInt;
    final val TOUCH_END = 2.toUInt;
    final val MOUSE_ENTER = 4.toUInt;
    final val MOUSE_LEAVE = 8.toUInt;
    final val CLICK = 16.toUInt;
}