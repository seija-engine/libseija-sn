package ui
import ui.core.{Font as FontAsset,FontAssetType};
import asset.Handle
import scala.collection.mutable.HashMap

case class Font(val handle:Handle[FontAsset]);

object Font {

    private val fontDict = new HashMap[String,Font]();
    private var defaultFont:Option[Font] = None;

    def load(name:String,path:String,isDefault:Boolean = false):Option[Font] = {
        val hFont = asset.Assets.loadSync[FontAsset](path);
        if(hFont.isEmpty) { return None };
        val newFont = Font(hFont.get);
        fontDict.put(name,newFont);
        if(isDefault) { defaultFont = Some(newFont) };
        Some(newFont)
    }

    def get(name:String):Option[Font] = fontDict.get(name)

    def getDefault_?() = defaultFont match {
        case Some(font) => font
        case None => { throw new Exception("Default font not set"); }
    }
}
