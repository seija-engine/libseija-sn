package ui.core

import core.RawComponentBuilder
import core.Entity
import core.RawComponent
import scala.scalanative.unsafe._
import math.RawVector4
import ui.core.FFISeijaUI
import scala.scalanative.unsigned._
import math.Vector4
import asset.Handle

class Text;

type RawText = CStruct4[RawVector4,Byte, Byte, Boolean];

enum AnchorAlign(val v: Byte) {
  case TopLeft extends AnchorAlign(0.toByte);
  case Top extends AnchorAlign(1.toByte);
  case TopRight extends AnchorAlign(2.toByte);
  case Left extends AnchorAlign(3.toByte);
  case Center extends AnchorAlign(4.toByte);
  case Right extends AnchorAlign(5.toByte);
  case BottomLeft extends AnchorAlign(6.toByte);
  case Bottom extends AnchorAlign(7.toByte);
  case BottomRight extends AnchorAlign(8.toByte);
}

enum LineMode(val v: Byte) {
  case SingleLine extends LineMode(0.toByte);
  case MultiLine extends LineMode(1.toByte);
}

class TextBuilder extends RawComponentBuilder {
  var fontSize: Int = 24;
  var anchor: AnchorAlign = AnchorAlign.Center;
  var lineMode: LineMode = LineMode.SingleLine;
  var isAutoSize: Boolean = true;
  var color: Vector4 = Vector4(1, 1, 1, 1);
  var font: Handle[Font] = null;
  var text: String = "";
  override def build(entity: Entity): Unit = {
    val ptrRawText = stackalloc[RawText]();
    ptrRawText._2 = this.anchor.v;
    ptrRawText._3 = this.lineMode.v;
    ptrRawText._4 = this.isAutoSize;
    math.Vector4RawFFI.toRaw(this.color, ptrRawText.at1);
    FFISeijaUI.entityAddText(core.App.worldPtr, entity.id, ptrRawText,fontSize,text,this.font.id.id)
  }
}

given TextComponent: RawComponent[Text] with {
  type BuilderType = TextBuilder;
  type RawType = Ptr[Byte];

  def builder(): BuilderType = new TextBuilder();
  def getRaw(entity: Entity): RawType = ???;
}
