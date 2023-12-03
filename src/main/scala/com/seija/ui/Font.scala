package com.seija.ui
import com.seija.ui.core.{Font as FontAsset,FontAssetType};
import com.seija.asset.Handle
import scala.collection.mutable.HashMap
import scala.util.Try
import scala.util.Success
import scala.util.Failure

case class Font(val handle:Handle[FontAsset]);

object Font {

    private val fontDict = new HashMap[String,Font]();
    private var defaultFont:Option[Font] = None;

    def load(name:String,path:String,isDefault:Boolean = false):Option[Font] = {
        val hFont = com.seija.asset.Assets.loadSync[FontAsset](path);
        if(hFont.isEmpty) { return None };
        val newFont = Font(hFont.get);
        fontDict.put(name,newFont);
        if(isDefault) { defaultFont = Some(newFont) };
        Some(newFont)
    }

    def get(name:String):Option[Font] = fontDict.get(name)

    def getDefault():Option[Font] = defaultFont
}
