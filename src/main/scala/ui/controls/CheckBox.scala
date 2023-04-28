package ui.controls
import ui.BaseControl
import ui.INotifyPropertyChanged
import ui.xml.IControlFromXml

class CheckBox extends BaseLayout {
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


given IControlFromXml[CheckBox] with {
    val name:String = "CheckBox"
    def create():CheckBox = new CheckBox()
    def setStringPropery(control:CheckBox,name:String,value:String):Unit = {
        import core.given
        
        given_IControlFromXml_BaseLayout.setStringPropery(control,name,value)
        name match
         case "checked" => control.checked = core.formString[Boolean](value).getOrElse(false)
         case _ => 
    }

}