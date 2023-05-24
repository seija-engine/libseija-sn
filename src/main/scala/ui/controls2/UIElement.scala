package ui.controls2
import ui.style.Style;
import core.Entity;
import ui.binding.INotifyPropertyChanged
import ui.core.LayoutAlignment
import ui.core.SizeValue
import ui.core.Thickness
import core.reflect.{autoProps,AutoGetSetter,ReflectType};
import scala.Conversion
import scala.quoted.Expr
import core.xml.XmlElement

class UIElement extends INotifyPropertyChanged derives ReflectType {
    protected var entity:Option[Entity] = None
    protected var style:Option[Style] = None
    protected var dataContext:Option[Any] = None;

    protected var _hor:LayoutAlignment = LayoutAlignment.Stretch
    protected var _ver:LayoutAlignment = LayoutAlignment.Stretch
    protected var _width:SizeValue = SizeValue.Auto
    protected var _height:SizeValue = SizeValue.Auto
    protected var _padding:Thickness = Thickness.zero
    protected var _margin:Thickness = Thickness.zero

    def hor = this._hor;
    def hor_=(value:LayoutAlignment) = { this._hor = value; this.callPropertyChanged("hor",this) }
    def ver = this._ver;
    def ver_=(value:LayoutAlignment) = { this._ver = value; this.callPropertyChanged("ver",this) }
    def width = this._width;
    def width_=(value:SizeValue) = { this._width = value; this.callPropertyChanged("width",this) }
    def height = this._height;
    def height_=(value:SizeValue) = { this._height = value; this.callPropertyChanged("height",this) }
    def padding = this._padding;
    def padding_=(value:Thickness) = { this._padding = value; this.callPropertyChanged("padding",this) }
    def margin = this._margin;
    def margin_=(value:Thickness) = { this._margin = value; this.callPropertyChanged("margin",this) }
    
    def handleXMLContent(xmlElement:XmlElement):XmlElement = { xmlElement }

    def Enter() = { 
    }

    def Exit() = {
    }
}