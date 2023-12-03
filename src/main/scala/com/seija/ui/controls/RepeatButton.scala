package com.seija.ui.controls
import com.seija.core.reflect.*;
import scala.scalanative.unsigned._
import com.seija.core.UpdateMgr
import com.seija.core.Time
import com.seija.ui.event.EventType

class RepeatButton extends ButtonBase derives ReflectType {
    var _delay:Float = 0;
    var _interval:Float = 0.3;
    def delay:Float = this._delay;
    def delay_=(value: Float): Unit = { this._delay = value; this.callPropertyChanged("delay",this); }
    def interval:Float = this._interval;
    def interval_=(value:Float): Unit = { this._interval = value;this.callPropertyChanged("interval",this); }

    private var _runInterval:Boolean = false;
    private var _remainDelay:Float = _delay;
    private var _remainInterval:Float = 0;
    override def OnEnter(): Unit = {
        super.OnEnter();
        UpdateMgr.add(this.OnUpdate);
    }

    override protected def onClick():Unit = {}

    override protected def onStartPressed(): Unit = {
        this._runInterval = true;
        this._remainDelay = this._delay;
    }

    override protected def onEndPressed(): Unit = {
        this._runInterval = false;
    }

    protected def OnUpdate(dt:Float):Unit = {
        if(this._runInterval == false) return;
        if(this._remainDelay > 0) {
            this._remainDelay -= dt;
            return;
        }
        if(this._remainInterval > 0) {
            this._remainInterval -= dt;
            return;
        }

        this._remainInterval = this._interval;
        this.callCommand();
    }

    override def Exit(): Unit = {
        super.Exit();
        UpdateMgr.remove(this.OnUpdate);
    }
}
