package ui.core
import core.RawComponentBuilder
import core.Entity
import core.RawComponent
import scala.scalanative.unsafe._
import ui.FFISeijaUI

class FlexLayout;

enum FlexDirection(val v:Byte) {
    case Row extends FlexDirection(0)
    case RowReverse extends FlexDirection(1)
    case Column extends FlexDirection(2)
    case ColumnReverse extends FlexDirection(3)
}

enum FlexWrap(val v:Byte) {
    case NoWrap extends FlexWrap(0)
    case Wrap extends FlexWrap(1)
}

enum FlexJustify(val v:Byte) {
    case Start extends FlexJustify(0)
    case Center extends FlexJustify(1)
    case End extends FlexJustify(2)
    case SpaceBetween extends FlexJustify(3)
    case SpaceAround extends FlexJustify(4)
}

enum FlexAlignItems(val v:Byte) {
    case Stretch extends FlexAlignItems(0)
    case Center extends FlexAlignItems(1)
    case Start extends FlexAlignItems(2)
    case End extends FlexAlignItems(3)
}

enum FlexAlignContent(val v:Byte) {
    case Stretch extends FlexAlignContent(0)
    case Center extends FlexAlignContent(1)
    case Start extends FlexAlignContent(2)
    case End extends FlexAlignContent(3)
    case SpaceBetween extends FlexAlignContent(4)
    case SpaceAround extends FlexAlignContent(5)
}

type RawFlexLayout = CStruct5[Byte,Byte,Byte,Byte,Byte]

class FlexLayoutBuilder extends RawComponentBuilder {
  var common:CommonView = CommonView();
  var direction:FlexDirection = FlexDirection.Row;
  var warp:FlexWrap = FlexWrap.NoWrap;
  var justify:FlexJustify = FlexJustify.Start;
  var align_items:FlexAlignItems = FlexAlignItems.Stretch;
  var align_content:FlexAlignContent = FlexAlignContent.Stretch;

  override def build(entity: Entity): Unit = {
    val flexPtr = stackalloc[RawFlexLayout]();
    flexPtr._1 = direction.v;
    flexPtr._2 = warp.v;
    flexPtr._3 = justify.v;
    flexPtr._4 = align_items.v;
    flexPtr._5 = align_content.v;
    FFISeijaUI.entityAddFlex(core.App.worldPtr, entity.id,common,flexPtr);
  }
}

given FlexLayoutComponent:RawComponent[FlexLayout] with {
  type BuilderType = FlexLayoutBuilder;
  override def builder(): BuilderType = new FlexLayoutBuilder()

  override def getRaw(entity: Entity): RawType = ???    
}