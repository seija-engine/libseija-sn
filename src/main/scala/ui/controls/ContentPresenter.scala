package ui.controls
import core.reflect.*;
import core.logError;
class ContentPresenter extends UIElement derives ReflectType {
    var content:Any = null
    var dataTemplate:Option[DataTemplate] = None
    override def OnEnter(): Unit = {
        this.initByContentControl();
        if(this.dataTemplate.isEmpty && this.content != null) {
            val dataType = this.content.getClass().getName();
            //println(s"find ${dataType} ${this.content.getClass()}")
            this.dataTemplate = this.findDataTemplate(dataType);
        }
        //println(this.dataTemplate);
        super.OnEnter();
        this.dataTemplate match {
            case Some(value) => {
                val newElement = value.LoadContent(this).logError().foreach(v => {
                    this.addChild(v);
                })
            }
            case None => {
                if(this.content != null) {
                    if(this.content.isInstanceOf[UIElement]) {
                        val contentElement = this.content.asInstanceOf[UIElement];
                        this.addChild(contentElement);
                    } else {
                        val stringValue = this.content.toString();
                        val textElement = new Text();
                        textElement.text = stringValue;
                        this.addChild(textElement);
                    }
                }
            }
        }
    }
    
    protected def initByContentControl():Unit = {
        if(this.templateParent.isDefined && this.templateParent.get.isInstanceOf[ContentControl]) {
            val parentContent = this.templateParent.get.asInstanceOf[ContentControl];
            if(this.content == null) {
               this.content = parentContent.content;
            }
            if(dataTemplate.isEmpty) {
                dataTemplate = parentContent.dataTemplate;
            }
            if(this.content != null) {
                this._dataContext = content;
            }
        }
    }
}