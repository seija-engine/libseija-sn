package com.seija.ui.core
import scalanative.unsafe._
import com.seija.core.RawComponentBuilder
import com.seija.core.Entity
import com.seija.core.RawComponent
import scala.scalanative.unsigned._
import com.seija.ui.core.FFISeijaUI


type RawEventNode = CStruct5[CUnsignedInt,CUnsignedInt,Boolean,Boolean,Boolean];


class EventNode;

class EventNodeBuilder extends RawComponentBuilder {
  var eventType:UInt = 0.toUInt;
  var stopCapture:Boolean = false;
  var stopBubble:Boolean = false;
  var useCapture:Boolean = true;
  override def build(entity: Entity): Unit = {
    val eventNodePtr = stackalloc[RawEventNode]();
    eventNodePtr._1 = EventNode.NONE;
    eventNodePtr._2 = this.eventType;
    eventNodePtr._3 = this.stopCapture;
    eventNodePtr._4 = this.stopBubble;
    eventNodePtr._5 = this.useCapture;
    FFISeijaUI.entityAddEventNode(com.seija.core.App.worldPtr,entity.id,eventNodePtr)
  }
}


object EventNode {
    final val NONE = 0.toUInt;
    final val TOUCH_START = 1.toUInt;
    final val TOUCH_END = 2.toUInt;
    final val MOUSE_ENTER = 4.toUInt;
    final val MOUSE_LEAVE = 8.toUInt;
    final val CLICK = 16.toUInt;

    given EventNodeComponent:RawComponent[EventNode] with {
      type BuilderType = EventNodeBuilder;
      type RawType = Option[Ptr[RawEventNode]];

      override def builder(): BuilderType = new EventNodeBuilder();

      override def getRaw(entity: Entity,isMut:Boolean): RawType = FFISeijaUI.entityGetEventNode(com.seija.core.App.worldPtr,entity)
    }
}