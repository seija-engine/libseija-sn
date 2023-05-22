package ui.controls

import ui.BaseControl
import core.IFromString
import ui.core.LayoutAlignment
import ui.core.SizeValue
import core.reflect.{TypeInfo,FieldInfo,ReflectType}
import ui.core.Thickness

class BaseLayout extends BaseControl with Cloneable derives ReflectType { 
    var _hor:LayoutAlignment = LayoutAlignment.Stretch
    var _ver:LayoutAlignment = LayoutAlignment.Stretch
    var _width:SizeValue = SizeValue.Auto
    var _height:SizeValue = SizeValue.Auto
    var _padding:Thickness = Thickness(0,0,0,0)
    
    def width = this._width;
    def width_= (value:SizeValue):Unit = {
      this._width = value; 
      this.callPropertyChanged("width",this)
    }
    def height = this._height;
    def height_= (value:SizeValue):Unit = { 
     this._height = value; 
     this.callPropertyChanged("height",this)
    }
    def hor = this._hor
    def hor_=(value:LayoutAlignment):Unit = { 
      this._hor = value;
      this.callPropertyChanged("hor",this) 
    }
    def ver = this._ver
    def ver_=(value:LayoutAlignment):Unit = { 
      this._ver = value;
      this.callPropertyChanged("ver",this) 
    }
    
    def padding = this._padding
    def padding_=(value:Thickness):Unit = { 
      this._padding = value;
      this.callPropertyChanged("padding",this) 
    }

    override def clone():BaseLayout = {
        val control:BaseLayout = super.clone().asInstanceOf[BaseLayout];
        
        control
    }
}

