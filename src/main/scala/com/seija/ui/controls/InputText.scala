package com.seija.ui.controls
import com.seija.core.reflect.ReflectType
import com.seija.ui.core.InputText as RawInputText;
import com.seija.math.Color
import com.seija.ui.event.EventManager
import com.seija.ui.event.EventType
import scala.scalanative.unsigned.UInt
import com.seija.ui.core.AnchorAlign
import scalanative.unsigned._
import com.seija.core.UpdateMgr
import com.seija.ui.core.FFISeijaUI
import com.seija.ui.event.RouteEvent
import com.seija.ui.event.RouteEventArgs

class InputText extends UIElement derives ReflectType {
    var _text: String = ""
    def text:String = this._text
    def text_=(value:String):Unit = {
        this._text = value; callPropertyChanged("text",this);
    }

    var _fontSize:Int = 24
    def fontSize:Int = this._fontSize
    def fontSize_=(value:Int):Unit = {
      this._fontSize = value;callPropertyChanged("fontSize",this)
    }

    var _caretColor:Color = Color.black
    def caretColor:Color = this._caretColor
    def caretColor_=(value:Color):Unit = {
      this._caretColor = value; callPropertyChanged("caretColor",this)
    }

    var _isActive:Boolean = false;

    private val _textComp:Text = new Text()

    override def Enter(): Unit = {
      this._textComp.hor = this._hor
      this._textComp.ver = this._ver
      this._textComp.width = this._width
      this._textComp.height = this._height
      this._textComp.padding = this._padding
      this._textComp.margin = this._margin
      this._textComp.fontSize = this.fontSize
      this._textComp.setParent(this.parent)
      this._textComp.color = Color.black
      this._textComp.anchor = AnchorAlign.Left
      this._textComp.isAutoSize = false
      this._textComp.Enter()
      super.Enter()
    }

    override def OnEnter(): Unit = {
      val newEntity = this.createBaseEntity(true)
      newEntity.add[RawInputText](builder => {
        builder.text = this._text
        builder.fontSize = this._fontSize
        builder.textEntity = Some(this._textComp.getEntity().get)
        builder.caretColor = this._caretColor
      })
      EventManager.register(newEntity,EventType.TOUCH_START,false,this.OnElementEvent)
      UpdateMgr.add(this.onUpdate);
    }

    protected def OnElementEvent(typ:UInt,px:Float,py:Float,args:Any):Unit = { }

    private def onUpdate(dt:Float):Unit = {
       val curEntity = this.getEntity().get
       val isActive = FFISeijaUI.inputGetIsActive(com.seija.core.App.worldPtr,curEntity)
       if(this._isActive != isActive) {
          this._isActive = isActive;
          this.routeEventController.fireEvent(InputTextEventArgs(this,this._isActive))
       }
       val isStringDirty = FFISeijaUI.inputReadStringDirty(com.seija.core.App.worldPtr,curEntity)
       if(isStringDirty) {
          val newString = FFISeijaUI.inputGetString(com.seija.core.App.worldPtr,curEntity)
          this.text = newString;
       }
    }

    override def Exit(): Unit = {
      super.Exit()
      EventManager.unRegister(this.entity.get);
      this._textComp.Exit()
      UpdateMgr.remove(this.onUpdate)
    }
}

class InputTextEventArgs(val source: UIElement,val isActive:Boolean) extends RouteEventArgs(InputText.ActiveEvent, false)
object InputText {
  val ActiveEvent: RouteEvent = RouteEvent("ActiveEvent", classOf[InputText])
}