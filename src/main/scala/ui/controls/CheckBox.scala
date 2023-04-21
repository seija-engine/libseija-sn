package ui.controls
import ui.BaseControl
import ui.INotifyPropertyChanged

class CheckBox extends BaseControl with INotifyPropertyChanged {
    protected var _checked: Boolean = false;

    def checked = this._checked
    def checked_=(value: Boolean): Unit = {
        this._checked = value;
        this.callPropertyChanged("checked")
    }

    override def OnEnter():Unit = {
        if(this.template.isEmpty) return;
        val template = this.template.get;
    }
}
