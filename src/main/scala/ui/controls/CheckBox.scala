package ui.controls
import ui.BaseControl
import ui.binding.INotifyPropertyChanged
import ui.xml.{IControlFromXml,setXmlStringPropery}
import scala.util.Try
import core.xml.XmlReader
import scala.util.Failure
import ui.Template
import ui.xml.XmlTemplateReader
import scala.util.Success
import core.Entity
import transform.{Transform}
import ui.core.Rect2D
import ui.core.ItemLayout
import ui.core.given;
import ui.EventManager

class CheckBox extends BaseLayout {
    var _checked: Boolean = false;
    protected var _template:Option[Template] = None;

    def checked = this._checked
    def checked_=(value: Boolean): Unit = {
        this._checked = value;
        this.callPropertyChanged("checked",this)
    }

    def template = this._template
    def template_=(value: Template): Unit = {
        this._template = Some(value);
    }

    override def OnEnter():Unit = {
        if(this.template.isEmpty) { return; }
        val template = this.template.get;
        val checkEntity = this.createEntity();
        EventManager.register(checkEntity,ui.EventType.CLICK,this.onClickCheckBox,"CQCQ");
        template.applyTo(this);
        
    }

    def onClickCheckBox(args:Any) = {
        this.checked = !this.checked;
    }

    def createEntity():Entity = {
        val parentEntity = this.parent.flatMap(_.getEntity());
        val entity = Entity.spawnEmpty()
          .add[Transform](_.parent = parentEntity)
          .add[Rect2D]()
          .add[ItemLayout](v => {
            v.common.hor = this._hor;
            v.common.ver = this._ver;
            v.common.uiSize.width = this._width;
            v.common.uiSize.height = this._height;
          })
        this.entity = Some(entity)
        return entity;
    }
}


given IControlFromXml[CheckBox] with {
    val name:String = "CheckBox"
    def create():CheckBox = new CheckBox()
    def setStringPropery(control:CheckBox,name:String,value:String):Unit = {
        import core.given
        setXmlStringPropery[BaseLayout](control,name,value)
        name match
         case "checked" => control.checked = core.formString[Boolean](value).getOrElse(false)
         case _ => 
    }

    override def readXmlProperty(control: CheckBox, reader: XmlReader): Try[Unit] = {
        XmlTemplateReader(reader,control).read().flatMap { template =>
          control.template = template;
          Success(())
        }
    }
}

import core.reflect.*;
given ReflectType[CheckBox] with {
  override def info: TypeInfo = TypeInfo("ui.controls.CheckBox",() => CheckBox(),Some(typeInfoOf[BaseLayout]),List(
     FieldInfo("checked",
                (a,b) => { a.asInstanceOf[CheckBox]._checked = b.asInstanceOf[Boolean] },
                _.asInstanceOf[CheckBox].checked),
  ))
}