package ui.controls
import core.reflect.*;
import core.logError;
class ContentPresenter extends UIElement derives ReflectType {
    var content:Option[Any] = None
    var dataTemplate:Option[DataTemplate] = None
    override def OnEnter(): Unit = {
        this.initByContentControl();
        if(this.dataTemplate.isEmpty && this.content.isDefined) {
            val dataType = this.content.get.getClass().getName();
            this.dataTemplate = this.findDataTemplate(dataType);
        }
        super.OnEnter();

        this.dataTemplate match {
            case Some(value) => {
                val newElement = value.LoadContent(this).logError().foreach(v => {
                    this.addChild(v);
                })
            }
            case None => {
                if(this.content.isDefined &&this.content.get.isInstanceOf[UIElement]) {
                    val contentElement = this.content.get.asInstanceOf[UIElement];
                    this.addChild(contentElement);
                }
            }
        }
    }
    
    protected def initByContentControl():Unit = {
        if(this.templateParent.isDefined && this.templateParent.get.isInstanceOf[ContentControl]) {
            val parentContent = this.templateParent.get.asInstanceOf[ContentControl];
            if(this.content.isEmpty) {
               this.content = parentContent.content;
            }
            if(dataTemplate.isEmpty) {
                dataTemplate = parentContent.dataTemplate;
            }
            if(content.isDefined) {
                this._dataContext = content;
            }
        }
    }
}