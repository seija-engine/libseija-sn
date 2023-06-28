package ui.controls
import core.reflect.*;
import ui.controls.Track;
class Slider extends RangeBase derives ReflectType {
    private val track:Track = Track();

    override def OnEnter(): Unit = {
        this.addChild(this.track);
    }
}
