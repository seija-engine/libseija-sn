package com.seija.ui.controls
import com.seija.ui.ContentProperty
import com.seija.core.reflect.ReflectType
import com.seija.ui.core.{FlexAlignContent, FlexAlignItems, FlexDirection, FlexJustify, FlexLayout, FlexLayoutBuilder, FlexWrap}
import com.seija.core.reflect.Into
import com.seija.ui.core.FlexItem
import com.seija.ui.core.FlexItemBuilder
import com.seija.ui.core.FlexAlignSelf
import com.seija.core.reflect.convert

@ContentProperty("children")
class FlexPanel extends Panel derives ReflectType {
  var _direction:FlexDirection = FlexDirection.Row
  def direction:FlexDirection = this._direction
  def direction_=(value:FlexDirection):Unit = {
    this._direction = value;callPropertyChanged("direction")
  }

  var _wrap:FlexWrap = FlexWrap.NoWrap
  def wrap:FlexWrap = this._wrap
  def wrap_=(value:FlexWrap):Unit = {
    this._wrap = value;callPropertyChanged("wrap")
  }

  var _justify:FlexJustify = FlexJustify.Start
  def justify:FlexJustify = this._justify
  def justify_=(value:FlexJustify):Unit = {
    this._justify = value;callPropertyChanged("justify")
  }

  var _alignItems:FlexAlignItems = FlexAlignItems.Start
  def alignItems:FlexAlignItems = this._alignItems
  def alignItems_=(value:FlexAlignItems):Unit = {
    this._alignItems = value;callPropertyChanged("alignItems")
  }

  var _alignContent:FlexAlignContent = FlexAlignContent.Start
  def alignContent:FlexAlignContent = this._alignContent
  def alignContent_=(value:FlexAlignContent):Unit = {
    this._alignContent = value; callPropertyChanged("alignContent")
  }


  override def OnEnter(): Unit = {
    val entity = this.createBaseEntity(false)
    entity.add[FlexLayout](builder => {
      builder.common.ver = this.ver;
      builder.common.hor = this.hor;
      builder.common.uiSize.width = this._width;
      builder.common.uiSize.height = this._height;
      builder.common.margin = this.margin;
      builder.common.padding = this.padding;
    

      builder.direction = this._direction
      builder.warp = this._wrap
      builder.justify = this._justify
      builder.alignItems = this._alignItems
      builder.alignContent = this._alignContent
    })
    this.checkAddCanvas()
  }
}

object FlexPanel {
  given Into[String,FlexItemBuilder] with {
    override def into(fromValue: String): FlexItemBuilder = {
      val builder = FlexItemBuilder()
      val propList = fromValue.split(",")
      for(propString <- propList) {
        val kvArray = propString.split("=");
        val propName = kvArray(0)
        val propValue = kvArray(1)
        propName match
          case "grow" => { builder.grow = propValue.toFloat }
          case "shrink" => { builder.shrink = propValue.toFloat }
          case "order" => { builder.order = propValue.toInt }
          case "basis" => { builder.basis = propValue.toFloat }
          case "alignSelf" => { builder.alignSelf = convert[String,FlexAlignSelf](propValue).get }
          case _ => slog.error(s"not match flexitem name:${propName}")
        
      }
      builder
    }
  }
}