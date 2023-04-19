package ui.core
import core.RawComponentBuilder
import core.Entity
import core.RawComponent
import scala.scalanative.unsafe._
import ui.core.FFISeijaUI

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

/*
#[derive(Clone, Copy,Hash,PartialEq, Eq)]
#[repr(C)]
pub enum FlexAlignSelf {
    Auto,
    Stretch,
    Center,
    Start,
    End
}
*/

enum FlexAlignSelf(val v:Byte) {
  case Auto extends FlexAlignSelf(0)
  case Stretch extends FlexAlignSelf(1)
  case Center extends FlexAlignSelf(2)
  case Start extends FlexAlignSelf(3)
  case End extends FlexAlignSelf(4)
}

type RawFlexLayout = CStruct5[Byte,Byte,Byte,Byte,Byte]

class FlexLayoutBuilder extends RawComponentBuilder {
  var common:CommonView = CommonView();
  var direction:FlexDirection = FlexDirection.Row;
  var warp:FlexWrap = FlexWrap.NoWrap;
  var justify:FlexJustify = FlexJustify.Start;
  var alignItems:FlexAlignItems = FlexAlignItems.Stretch;
  var alignContent:FlexAlignContent = FlexAlignContent.Stretch;

  override def build(entity: Entity): Unit = {
    val flexPtr = stackalloc[RawFlexLayout]();
    flexPtr._1 = direction.v;
    flexPtr._2 = warp.v;
    flexPtr._3 = justify.v;
    flexPtr._4 = alignItems.v;
    flexPtr._5 = alignContent.v;
    FFISeijaUI.entityAddFlex(core.App.worldPtr, entity.id,common,flexPtr);
  }
}

given FlexLayoutComponent:RawComponent[FlexLayout] with {
  type BuilderType = FlexLayoutBuilder;
  override def builder(): BuilderType = new FlexLayoutBuilder()

  override def getRaw(entity: Entity): RawType = ???    
}


class FlexItem;

type RawFlexItem = CStruct6[Int,Float,Float,Float,Boolean,Byte]
class FlexItemBuilder extends RawComponentBuilder {

  var order:Int = 0;
  var grow:Float = 0;
  var shrink:Float = 0;
  var basis:Float = 0;
  var alignSelf:FlexAlignSelf = FlexAlignSelf.Auto
  override def build(entity: Entity): Unit = {
      val rawFlexItemPtr = stackalloc[RawFlexItem]()
      rawFlexItemPtr._1 = order;
      rawFlexItemPtr._2 = grow;
      rawFlexItemPtr._3 = shrink;
      rawFlexItemPtr._4 = basis;
      rawFlexItemPtr._5 = false;
      rawFlexItemPtr._6 = alignSelf.v
      FFISeijaUI.entityAddFlexItem(core.App.worldPtr,entity.id,rawFlexItemPtr)
  }
}

given FlexItemComponent:RawComponent[FlexItem] with {
  type BuilderType = FlexItemBuilder;
  override def builder(): BuilderType = new FlexItemBuilder()

  override def getRaw(entity: Entity): RawType = ???
}