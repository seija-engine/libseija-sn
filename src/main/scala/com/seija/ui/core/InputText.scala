package com.seija.ui.core
import com.seija.core.{Entity, RawComponentBuilder,RawComponent}
import com.seija.math.Color
import scalanative.unsafe._
import com.seija.core.App
class InputText

class InputTextBuilder extends RawComponentBuilder {
  var fontSize:Int = 24
  var textEntity:Option[Entity] = None
  var text:String = ""
  var caretColor:Color = Color.black

  def build(entity: Entity): Unit = {
    FFISeijaUI.entityAddInput(App.worldPtr,entity,textEntity.getOrElse(Entity(0)),fontSize,caretColor,text)
  }
}

type RawInputTextFFI = CStruct1[Int]

case class RawInputText(ptr:Ptr[RawInputTextFFI]) {
  def setText(value:String):Unit = {
    FFISeijaUI.inputSetString(ptr,value)
  }
}

object InputText {
  given InputTextComponent:RawComponent[InputText] with {
    type BuilderType = InputTextBuilder
    type RawType = RawInputText
    override def getRaw(entity: Entity, isMut: Boolean): RawInputText = RawInputText(FFISeijaUI.entityGetInput(App.worldPtr,entity))

    override def builder(): InputTextBuilder = new InputTextBuilder()
  }
}