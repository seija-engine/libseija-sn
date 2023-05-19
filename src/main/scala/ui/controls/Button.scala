package ui.controls
import core.reflect.ReflectType;
import core.formString;
import math.Color
import ui.Template
import core.Entity
import transform.Transform;
import ui.core.{Rect2D,ItemLayout};
import scala.util.Try
import core.xml.XmlReader
import ui.xml.XmlTemplateReader
import scala.util.Success
import ui.core.CommonViewStates

class Button extends BaseLayout derives ReflectType {
    protected var _template:Option[Template] = None;
    def template = this._template
    def template_=(value: Template): Unit = {  this._template = Some(value); }
    var _content:String = "OK";
    def content = this._content;
    def content_=(value:String) = {
        this._content = value; this.callPropertyChanged("content",this)
    }

    private var _viewState:CommonViewStates = CommonViewStates.Normal;

    override def OnEnter(): Unit = {
        if(this.template.isEmpty) { return; }
        val createEntity = this.createEntity();
        this.template.get.applyTo(this);
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