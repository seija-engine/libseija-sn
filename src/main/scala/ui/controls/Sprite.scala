package ui.controls
import ui.core._
import ui.BaseControl

class Sprite extends BaseControl {
  var hor:LayoutAlignment = LayoutAlignment.Stretch
  var ver:LayoutAlignment = LayoutAlignment.Stretch
  var width:SizeValue = SizeValue.Auto
  var height:SizeValue = SizeValue.Auto

  def OnEnter(): Unit = {

  }

  

  def OnExit(): Unit = {
    
  }
}
