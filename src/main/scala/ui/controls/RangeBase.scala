package ui.controls
import core.reflect.*;

class RangeBase extends Control derives ReflectType {
    protected var _value:Float = 0;
    def value:Float = this._value;
    def value_=(num:Float) = { this._value = num; this.callPropertyChanged("value",this) }
    
}
