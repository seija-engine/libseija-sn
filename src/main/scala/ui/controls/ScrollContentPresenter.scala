package ui.controls
import core.reflect.*;

class ScrollContentPresenter extends UIElement derives ReflectType {
    override def OnEnter():Unit = {
        this.hookupScrollingComponents();
        super.OnEnter();
         
    }

    def hookupScrollingComponents():Unit = {
        if(this.templateParent.isEmpty || !this.templateParent.get.isInstanceOf[ScrollViewer]) {
            return;
        }
        val scrollView = this.templateParent.get.asInstanceOf[ScrollViewer];
        var si:Option[IScrollInfo] = None;
        
    }
}