package ui
import _root_.core.App;
import ui.core.SpriteSheet;
import scalanative.unsafe._
import scalanative.runtime._
import asset.{Handle,Assets};
import ui.core.{FFISeijaUI,given}
import java.util.HashMap;
import scala.scalanative.unsigned._
import scala.scalanative.runtime.libc
case class Atlas(val sheet:Handle[SpriteSheet]) {
  private var sprites = new HashMap[String,AtlasSprite]();

  def get(name:String):Option[AtlasSprite] = Option(sprites.get(name))
}

object Atlas {
    private var atlasDict = new HashMap[String,Atlas]();

    def load(name:String,path:String):Option[Atlas] = {
        val hSheet = Assets.loadSync[SpriteSheet]("ui/default.json");
        val newAtlas = hSheet.map(Atlas.apply);
        newAtlas.foreach(atlas => atlasDict.put(name,atlas));
        newAtlas
    }

    def apply(hSheet:Handle[SpriteSheet]):Atlas = {
        val atlas = new Atlas(hSheet);
        val rawSheet = FFISeijaUI.spriteSheetAssetGet(App.worldPtr, hSheet.id.id);
        var (ptr,count,charMax) = FFISeijaUI.spriteBeginRead(rawSheet);
        charMax += 4;
        val ptrStringBuffer:CString = stackalloc[CChar](charMax);
        for (i <- 0 until count) {
            libc.memset(toRawPtr(ptrStringBuffer),0,charMax.toULong);
            val sprite = FFISeijaUI.spriteSheetGetInfo(ptr,i,ptrStringBuffer);
            val atlasSprite = AtlasSprite(sprite._1,atlas,sprite._2);
            atlas.sprites.put(sprite._2,atlasSprite);
            //println(atlasSprite)
        }
        FFISeijaUI.spriteEndRead(ptr);
        atlas
    }
}


case class AtlasSprite(val index:Int,val atlas:Atlas, val name:String);