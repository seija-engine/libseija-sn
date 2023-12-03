package com.seija.ui.core
import com.seija.core.RawComponentBuilder
import com.seija.core.Entity
import com.seija.core.RawComponent
import com.seija.core.reflect.{Into, TypeCastException}

import scala.scalanative.unsafe.*
import com.seija.ui.core.FFISeijaUI

class FlexLayout

object FlexLayout {
  given FlexLayoutComponent: RawComponent[FlexLayout] with {
    type BuilderType = FlexLayoutBuilder

    override def builder(): BuilderType = new FlexLayoutBuilder()

    override def getRaw(entity: Entity, isMut: Boolean): RawType = ???
  }
}

enum FlexDirection(val v:Byte) {
    case Row extends FlexDirection(0)
    case RowReverse extends FlexDirection(1)
    case Column extends FlexDirection(2)
    case ColumnReverse extends FlexDirection(3)
}

object FlexDirection {
  given Into[String, FlexDirection] with {
    override def into(fromValue: String): FlexDirection = fromValue match {
      case "Row" => FlexDirection.Row
      case "RowReverse" => FlexDirection.RowReverse
      case "Column" => FlexDirection.Column
      case "ColumnReverse" => FlexDirection.ColumnReverse
      case _:String => throw TypeCastException("String", "FlexDirection")
    }
  }
}

enum FlexWrap(val v:Byte) {
    case NoWrap extends FlexWrap(0)
    case Wrap extends FlexWrap(1)
}

object FlexWrap {
  given Into[String,FlexWrap] with {
    override def into(fromValue: String): FlexWrap = fromValue match {
      case "Wrap" => FlexWrap.Wrap
      case "NoWrap" => FlexWrap.NoWrap
      case _: String => throw TypeCastException("String", "FlexWrap")
    }
  }
}

enum FlexJustify(val v:Byte) {
    case Start extends FlexJustify(0)
    case Center extends FlexJustify(1)
    case End extends FlexJustify(2)
    case SpaceBetween extends FlexJustify(3)
    case SpaceAround extends FlexJustify(4)
}

object FlexJustify {
  given Into[String,FlexJustify] with {
    override def into(fromValue: String): FlexJustify = fromValue match {
      case "Start" => FlexJustify.Start
      case "Center" => FlexJustify.Center
      case "End" => FlexJustify.End
      case "SpaceBetween" => FlexJustify.SpaceBetween
      case "SpaceAround" => FlexJustify.SpaceAround
      case _ => throw TypeCastException("String","FlexJustify")
    }
  }
}

enum FlexAlignItems(val v:Byte) {
    case Stretch extends FlexAlignItems(0)
    case Center extends FlexAlignItems(1)
    case Start extends FlexAlignItems(2)
    case End extends FlexAlignItems(3)
}

object FlexAlignItems {
  given Into[String,FlexAlignItems] with {
    override def into(fromValue: String): FlexAlignItems = fromValue match
      case "Start" => FlexAlignItems.Start
      case "End" => FlexAlignItems.End
      case "Center" => FlexAlignItems.Center
      case "Stretch" => FlexAlignItems.Stretch
      case _ => throw TypeCastException("String","FlexAlignItems")
  }
}

enum FlexAlignContent(val v:Byte) {
    case Stretch extends FlexAlignContent(0)
    case Center extends FlexAlignContent(1)
    case Start extends FlexAlignContent(2)
    case End extends FlexAlignContent(3)
    case SpaceBetween extends FlexAlignContent(4)
    case SpaceAround extends FlexAlignContent(5)
}

object FlexAlignContent {
  given Into[String,FlexAlignContent] with {
    override def into(fromValue: String): FlexAlignContent = fromValue match
      case "Start" => FlexAlignContent.Start
      case "Center" => FlexAlignContent.Center
      case "End" => FlexAlignContent.End
      case "Stretch" => FlexAlignContent.Stretch
      case "SpaceBetween" => FlexAlignContent.SpaceBetween
      case "SpaceAround" => FlexAlignContent.SpaceAround
      case _ => throw TypeCastException("String","FlexAlignContent")
  }
}


enum FlexAlignSelf(val v:Byte) {
  case Auto extends FlexAlignSelf(0)
  case Stretch extends FlexAlignSelf(1)
  case Center extends FlexAlignSelf(2)
  case Start extends FlexAlignSelf(3)
  case End extends FlexAlignSelf(4)
}

object FlexAlignSelf {
  given Into[String,FlexAlignSelf] with {
    override def into(fromValue: String): FlexAlignSelf = fromValue match
      case "Auto" => FlexAlignSelf.Auto
      case "Stretch" => FlexAlignSelf.Stretch
      case "Center" => FlexAlignSelf.Center
      case "End" => FlexAlignSelf.End
      case "Start" => FlexAlignSelf.Start
      case _ => throw TypeCastException("String","FlexAlignSelf")
  }
}

type RawFlexLayout = CStruct5[Byte,Byte,Byte,Byte,Byte]

class FlexLayoutBuilder extends RawComponentBuilder {
  var common:CommonView = CommonView()
  var direction:FlexDirection = FlexDirection.Row
  var warp:FlexWrap = FlexWrap.NoWrap
  var justify:FlexJustify = FlexJustify.Start
  var alignItems:FlexAlignItems = FlexAlignItems.Stretch
  var alignContent:FlexAlignContent = FlexAlignContent.Stretch

  override def build(entity: Entity): Unit = {
    val flexPtr = stackalloc[RawFlexLayout]();
    flexPtr._1 = direction.v;
    flexPtr._2 = warp.v;
    flexPtr._3 = justify.v;
    flexPtr._4 = alignItems.v;
    flexPtr._5 = alignContent.v;
    FFISeijaUI.entityAddFlex(com.seija.core.App.worldPtr, entity.id,common,flexPtr);
  }
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
      FFISeijaUI.entityAddFlexItem(com.seija.core.App.worldPtr,entity.id,rawFlexItemPtr)
  }
}

given FlexItemComponent:RawComponent[FlexItem] with {
  type BuilderType = FlexItemBuilder;
  override def builder(): BuilderType = new FlexItemBuilder()

  override def getRaw(entity: Entity,isMut:Boolean): RawType = ???
}