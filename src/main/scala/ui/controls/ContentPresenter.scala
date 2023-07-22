package ui.controls
import core.reflect.*;
import core.logError;

class ContentPresenter extends UIElement derives ReflectType {
    var content:Any = _
    var contentTemplate:Option[DataTemplate] = None
    protected var _contentSource:String = "content"
    def contentSource:String = this._contentSource
    def contentSource_=(value:String):Unit = {
      this._contentSource = value; callPropertyChanged("contentSource",this)
    }

    override def OnEnter(): Unit = {
        this.initByContentSource();
        if(this.contentTemplate.isEmpty && this.content != null) {
            val dataType = this.content.getClass.getName;
            this.contentTemplate = this.findDataTemplate(dataType);
        }
        super.OnEnter();
        this.createByDataTemplate();
    }

    protected def createByDataTemplate():Unit = {
        this.contentTemplate match {
            case Some(value) => {
                value.LoadContent(this,None).logError().foreach(v => {
                    this.addChild(v);
                })
            }
            case None =>
              if(this.content != null) {
                this.content match {
                    case contentElement: UIElement =>
                      this.addChild(contentElement)
                    case _ =>
                      val stringValue = this.content.toString
                      val textElement = new Text()
                      textElement.text = stringValue
                      this.addChild(textElement)
                }
              }
        }
    }
    
    protected def initByContentSource():Unit = {
      if(this.templateParent.isEmpty) return
      val parentElement:UIElement =  this.templateParent.get
      val typInfo = Assembly.getTypeInfo(parentElement)
      if(typInfo.isEmpty) return
      val strContentKey = contentSource
      val strContentTemplate = s"${contentSource}Template"
      if(this.content == null) {
        typInfo.get.getField(strContentKey).foreach {info =>
          this.content = info.get(parentElement)
        }
      }
      if(this.contentTemplate.isEmpty) {
        typInfo.get.getField(strContentTemplate).foreach {info =>
          this.contentTemplate = info.get(parentElement).asInstanceOf[Option[DataTemplate]]
        }
      }
      if (this.content != null) {
        this._dataContext = content;
      }
    }
}