package ui.controls
import core.reflect._
import scala.scalanative.unsigned.UInt
import ui.visualState.ViewStates

class ToggleButton extends ButtonBase derives ReflectType {
    protected var _isChecked:Boolean = false

    def isChecked:Boolean = this._isChecked
    def isChecked_=(value:Boolean):Unit = {
        this._isChecked = value; callPropertyChanged("isChecked",this)
    }

    override protected def onClick(): Unit = {
        super.onClick()
        this.isChecked = !this.isChecked;
        this.updateCheckViewState()
    }

    override def updateVisualState(): Unit = {
        super.updateVisualState()
        this.updateCheckViewState()
    }

    protected def updateCheckViewState():Unit = {
        if(this._isChecked) {
            this.setViewState(ViewStates.CheckStates,ViewStates.Checked)
        } else {
            this.setViewState(ViewStates.CheckStates,ViewStates.Unchecked)
        }
    }
}