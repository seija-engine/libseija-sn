package ui.controls
import ui.BaseControl
import ui.binding.INotifyPropertyChanged
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
import core.reflect.ReflectType

class CheckBox extends BaseLayout derives ReflectType {
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

    override def readXmlProperty(startName: String, reader: XmlReader): Unit = {
        XmlTemplateReader(reader,this).read().flatMap { template =>
          this.template = template;
          Success(())
        }
    }
}