package ui.controls
import core.reflect.*;

class RangeBase extends Control derives ReflectType {
    protected var _value:Float = 0;
    protected var _maximum:Float = 0;
    protected var _minimum:Float = 0;
    def value:Float = this._value;
    def maximum:Float = this._maximum;
    def minimum:Float = this._minimum;
    def value_=(num:Float):Unit = { this._value = num; this.callPropertyChanged("value",this); }
    def maximum_=(value:Float):Unit = { this._maximum = value; callPropertyChanged("maximum",this); }
    def minimum_=(value:Float):Unit = { this._minimum = value; callPropertyChanged("minimum",this); }
}
