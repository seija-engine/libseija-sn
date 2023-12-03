package com.seija.ui.core
import com.seija.core.RawComponent
import com.seija.core.Entity
import com.seija.core.RawComponentBuilder
import scalanative.libc.stddef;
import scala.scalanative.unsafe.Ptr
import com.seija.math.RawVector2
import com.seija.math.Vector2
class FreeLayout;

object FreeLayout {
  given FreeLayoutComponent: RawComponent[FreeLayout] with {
    type BuilderType = FreeLayoutBuilder;
    type RawType = Ptr[Byte]
    override def builder(): BuilderType = new FreeLayoutBuilder()

    override def getRaw(entity: Entity,isMut:Boolean): RawType = stddef.NULL
  }
}

class FreeLayoutBuilder extends RawComponentBuilder {
    var common: CommonView = CommonView()
    override def build(entity: Entity): Unit = {
      FFISeijaUI.entityAddFreeLayout(com.seija.core.App.worldPtr, entity.id, common);
    }
}

class FreeLayoutItem;

object FreeLayoutItem {
  given FreeLayoutItemComponent: RawComponent[FreeLayoutItem] with {
    type BuilderType = FreeLayoutItemBuilder;
    type RawType = Ptr[RawVector2]
    override def builder(): BuilderType = new FreeLayoutItemBuilder()

    override def getRaw(entity: Entity,isMut:Boolean): RawType = {
      FFISeijaUI.entityGetFreeItem(com.seija.core.App.worldPtr,entity.id)
    }
  }
}

class FreeLayoutItemBuilder extends RawComponentBuilder {
    var pos:Vector2 = Vector2.zero.clone();
    override def build(entity: Entity): Unit = {
      FFISeijaUI.entityAddFreeItem(com.seija.core.App.worldPtr, entity.id,pos.x,pos.y);
    }
}
