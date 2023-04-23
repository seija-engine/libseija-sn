package ui.usl.types

import ui.usl.{UDSLType,UDSL,TypeInfo,ClassType,EnumItem}
import ui.controls.Image
import ui.core.LayoutAlignment
import ui.core.SizeValue
import ui.AtlasSprite
import scala.collection.mutable

given ImageUDSL: UDSLType[Image] with {
  
  val typInfo = TypeInfo.Class(
    ClassType[Image]("Image")
                    .field[LayoutAlignment]("hor",LayoutAlignmentUDSL.typInfo,_.hor_=(_))
                    .field[LayoutAlignment]("ver",LayoutAlignmentUDSL.typInfo,_.ver_=(_))
                    .field[SizeValue]("width",SizeValueUDSL.typInfo,_.width_=(_))
                    .field[SizeValue]("height",SizeValueUDSL.typInfo,_.height_=(_))
                    .field[Option[AtlasSprite]]("sprite",AtlasSpriteUDSL.typInfo,_.sprite_=(_))
  )

  def default() = Image()
}

given LayoutAlignmentUDSL: UDSLType[LayoutAlignment] with {
  val typInfo = TypeInfo.NumberEnum("LayoutAlignment")
  def default() = LayoutAlignment.Start
 
  override def fromNumEnum(tag:Int) = LayoutAlignment.fromOrdinal(tag)
}


given SizeValueUDSL: UDSLType[SizeValue] with {
  val typInfo = TypeInfo.Enum(List(
    EnumItem("Auto", None),
    EnumItem("FormRect", None),
    EnumItem("Pixel", Some(TypeInfo.Float)),
  ))
  
  def default() = SizeValue.Auto

  override def fromEnum(tag: Int, args: List[Any]): SizeValue = {
    tag match {
      case 0 => SizeValue.Auto
      case 1 => SizeValue.FormRect
      case 2 => SizeValue.Pixel(args(0).asInstanceOf[Float])
    }
  }
}

given AtlasSpriteUDSL:UDSLType[AtlasSprite] with {
  val typInfo = TypeInfo.Class(ClassType[AtlasSprite]("AtlasSprite")
      .field("AtlasName",TypeInfo.String,null)
      .field("SpriteName",TypeInfo.String,null)
  )

  override def fromClass(args: mutable.HashMap[String, Any]): Option[AtlasSprite] = {
    val atlasName = args("AtlasName").asInstanceOf[String]
    val spriteName = args("SpriteName").asInstanceOf[String]
    val sprite = ui.Atlas.get(atlasName).flatMap(_.get(spriteName));
    sprite
  }

  def default() = AtlasSprite(0,null,"")
}