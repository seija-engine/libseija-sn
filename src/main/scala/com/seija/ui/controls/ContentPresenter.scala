package com.seija.ui.controls;
import com.seija.core.logError;
import com.seija.ui.binding.DataBindingManager
import com.seija.ui.binding.{BindingItem,BindingType,BindingSource}
import com.seija.core.reflect.Assembly
import com.seija.core.reflect.ReflectType

class ContentPresenter extends UIElement derives ReflectType {
    var content:Any = _
    var contentTemplate:Option[DataTemplate] = None
    
    protected var _contentSource:String = "content"
    def contentSource:String = this._contentSource
    def contentSource_=(value:String):Unit = {
      this._contentSource = value; callPropertyChanged("contentSource",this)
    }

    override def OnEnter(): Unit = {
        this.attachSource();
        
        super.OnEnter()
        this.createByDataTemplate()
    }

    protected def createByDataTemplate():Unit = {
        if(this.contentTemplate.isEmpty && this.content != null) {
            val dataType = this.content.getClass.getName;
            this.contentTemplate = this.findDataTemplate(dataType);
        }
        this.contentTemplate match {
            case Some(value) => {
                value.LoadContent(this,None).logError().foreach(v => {
                    this.addChild(v)
                    v.setLogicParent(this.templateParent)
                })
            }
            case None =>
              if(this.content != null) {
                this.content match {
                    case contentElement: UIElement =>
                      this.addChild(contentElement)
                      contentElement.setLogicParent(this.templateParent)
                    case _ =>
                      val stringValue = this.content.toString
                      val textElement = new Text()
                      textElement.text = stringValue
                      this.addChild(textElement)
                      textElement.setLogicParent(this.templateParent)
                }
              }
        }
    }
    
    protected def attachSource():Unit = {
      if(this.templateParent.isEmpty) {
        if(this.dataContext != null) { this.content = this.dataContext  }
        return
      }

      val parentElement:UIElement =  this.templateParent.get
      val typInfo = Assembly.getTypeInfo(parentElement)
      if(typInfo.isEmpty) return
      val strContentKey = contentSource
      val strContentTemplate = s"${contentSource}Template"
      if(this.content == null) {
        typInfo.get.getField(strContentKey).foreach {info =>
          this.content = info.get(parentElement)
        }
        val inst = DataBindingManager.binding(parentElement,this,BindingItem(BindingSource.Owner,strContentKey,"content",None,BindingType.Src2Dst)).get
        inst.foreach {v => this.bindingInstList += v }
      }
      if(this.contentTemplate.isEmpty) {
        typInfo.get.getField(strContentTemplate).foreach {info =>
          this.contentTemplate = info.get(parentElement).asInstanceOf[Option[DataTemplate]]
        }
      }
      if (this.content != null) {
        this._dataContext = content;
      }
      if(this.content == null) {
        this.content = this.dataContext;
      }
    }


    def PrepareContentPresenter(itemData:Any,itemTemplate:Option[DataTemplate]):Unit = {
      if(itemTemplate.isDefined) {
        this.contentTemplate = itemTemplate
      }
    }

    override def onPropertyChanged(propertyName: String): Unit = {
      super.onPropertyChanged(propertyName)
      propertyName match
        case "content" => this.onContentChanged()
        case _ =>
    }

    def onContentChanged():Unit = {
      this.content match
        case contentElement:UIElement => {
            this.children.headOption.foreach {v => 
               v.Release();
               this.children.clear();  
            }
            val newElement = contentElement.clone();
            this.addChild(newElement)
            newElement.setLogicParent(this.templateParent)
            newElement.Enter()
        }
        case _ => 
      
    }
}