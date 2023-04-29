package ui.controls
import ui.BaseControl
import ui.INotifyPropertyChanged
import ui.xml.IControlFromXml
import scala.util.Try
import core.xml.XmlReader
import scala.util.Failure
import ui.Template
import ui.xml.XmlTemplateReader
import scala.util.Success

class CheckBox extends BaseLayout {
    protected var _checked: Boolean = false;
    protected var _template:Option[Template] = None;

    def checked = this._checked
    def checked_=(value: Boolean): Unit = {
        this._checked = value;
        this.callPropertyChanged("checked")
    }

    def template = this._template
    def template_=(value: Template): Unit = {
        this._template = Some(value);
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

    override def readXmlProperty(control: CheckBox, reader: XmlReader): Try[Unit] = {
        XmlTemplateReader(reader).read().flatMap { template =>
          control.template = template;
          Success(())
        }
    }

}