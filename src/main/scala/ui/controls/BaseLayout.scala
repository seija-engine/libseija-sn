package ui.controls

import ui.BaseControl
import core.IFromString
import ui.core.LayoutAlignment
import ui.core.SizeValue
import ui.core.{given_IFromString_LayoutAlignment};
import ui.xml.IControlFromXml

class BaseLayout extends BaseControl with Cloneable {
    protected var _hor:LayoutAlignment = LayoutAlignment.Stretch
    protected  var _ver:LayoutAlignment = LayoutAlignment.Stretch
    protected  var _width:SizeValue = SizeValue.Auto
    protected  var _height:SizeValue = SizeValue.Auto
    
    def width = this._width;
    def width_= (value:SizeValue):Unit = { 
      this._width = value; 
      this.callPropertyChanged("width")
    }
    def height = this._height;
    def height_= (value:SizeValue):Unit = { 
     this._height = value; 
     this.callPropertyChanged("height")
    }
    def hor = this._hor
    def hor_=(value:LayoutAlignment):Unit = { 
      this._hor = value;
      this.callPropertyChanged("hor") 
    }
    def ver = this._ver
    def ver_=(value:LayoutAlignment):Unit = { 
      this._ver = value;
      this.callPropertyChanged("ver") 
    }

    override def clone():BaseLayout = {
        val control:BaseLayout = super.clone().asInstanceOf[BaseLayout];
        
        control
    }
}



given IControlFromXml[BaseLayout] with {
    val name:String = "BaseLayout"
    def create():BaseLayout = new BaseLayout()
    def setStringPropery(control:BaseLayout,name:String,value:String):Unit = {
       import ui.core._
       import ui.core.given;
       name match
        case "width"  => control.width = core.formString[SizeValue](value).get
        case "height" => control.height = core.formString[SizeValue](value).get
        case "hor" => control.hor = core.formString[LayoutAlignment](value).get
        case "ver" => control.ver = core.formString[LayoutAlignment](value).get
        case _ => {}

      
    }

}