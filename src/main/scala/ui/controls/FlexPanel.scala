package ui.controls
import ui.ContentProperty
import core.reflect.ReflectType
import ui.core.{FlexAlignContent, FlexAlignItems, FlexDirection, FlexJustify, FlexLayout, FlexLayoutBuilder, FlexWrap}

@ContentProperty("children")
class FlexPanel extends Panel derives ReflectType {
  var _direction:FlexDirection = FlexDirection.Row
  def direction:FlexDirection = this._direction
  def direction_=(value:FlexDirection):Unit = {
    this._direction = value;callPropertyChanged("direction",this)
  }

  var _wrap:FlexWrap = FlexWrap.NoWrap
  def wrap:FlexWrap = this._wrap
  def wrap_=(value:FlexWrap):Unit = {
    this._wrap = value;callPropertyChanged("wrap",this)
  }

  var _justify:FlexJustify = FlexJustify.Start
  def justify:FlexJustify = this._justify
  def justify_=(value:FlexJustify):Unit = {
    this._justify = value;callPropertyChanged("justify",this)
  }

  var _alignItems:FlexAlignItems = FlexAlignItems.Start
  def alignItems:FlexAlignItems = this._alignItems
  def alignItems_=(value:FlexAlignItems):Unit = {
    this._alignItems = value;callPropertyChanged("alignItems",this)
  }

  var _alignContent:FlexAlignContent = FlexAlignContent.Start
  def alignContent:FlexAlignContent = this._alignContent
  def alignContent_=(value:FlexAlignContent):Unit = {
    this._alignContent = value; callPropertyChanged("alignContent",this)
  }


  override def OnEnter(): Unit = {
    val entity = this.createBaseEntity(false)
    entity.add[FlexLayout](builder => {
      builder.direction = this._direction
      builder.warp = this._wrap
      builder.justify = this._justify
      builder.alignItems = this._alignItems
      builder.alignContent = this._alignContent
    })
    this.checkAddCanvas()
  }

}