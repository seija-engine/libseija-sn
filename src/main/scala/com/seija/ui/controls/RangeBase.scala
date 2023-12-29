package com.seija.ui.controls
import com.seija.core.reflect.*;

class RangeBase extends Control derives ReflectType {
    protected var _value:Float = 0;
    protected var _maxValue:Float = 1;
    protected var _minValue:Float = 0;
    //region Setter

    def value:Float = this._value;
    def maxValue:Float = this._maxValue;
    def minValue:Float = this._minValue;
    def value_=(num:Float):Unit = { this._value = num; this.callPropertyChanged("value"); }
    def maxValue_=(value:Float):Unit = { this._maxValue = value; callPropertyChanged("maxValue"); }
    def minValue_=(value:Float):Unit = { this._minValue = value; callPropertyChanged("minValue"); }
    //endregion

    protected def clipValue(newValue:Float):Float = {
      if(newValue < this._minValue) {
         return this._minValue
      }
      if(newValue > this._maxValue) {
        return  this._maxValue
      }
      newValue
    }
}
