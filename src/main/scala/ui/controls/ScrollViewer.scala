package ui.controls
import core.UpdateMgr
import core.reflect.*
import ui.UIModule
import ui.core.{Rect2D, Rect2DBuilder}
import ui.event.RouteEventArgs;

class ScrollViewer extends ContentControl derives ReflectType {
  protected var _scrollableHeight:Float = 0f
  protected var _scrollableWidth:Float = 0f
  protected var _horizontalOffset:Float = 0f
  protected var _verticalOffset:Float = 0f
  protected var _viewportWidth:Float = 0f
  protected var _viewportHeight:Float = 0f
  protected var _barWidth:Float = 0f
  protected var _barHeight:Float = 0f

  private var scrollContent:Option[ScrollContentPresenter] = None

  //region Setter

  def scrollableHeight:Float = this._scrollableHeight
  def scrollableWidth:Float = this._scrollableWidth
  def horizontalOffset:Float = this._horizontalOffset
  def verticalOffset:Float = this._verticalOffset
  def viewportWidth:Float = this._viewportWidth
  def viewportHeight:Float = this._viewportHeight
  def barWidth: Float = this._barWidth
  def barHeight: Float = this._barHeight
  def scrollableHeight_=(value: Float): Unit = {
    this._scrollableHeight = value;
    this.callPropertyChanged("scrollableHeight",this);
  }
  def scrollableWidth_=(value:Float):Unit = {
    this._scrollableWidth = value;
    this.callPropertyChanged("scrollableWidth",this);
  }
  def verticalOffset_=(value:Float):Unit = {
    this._verticalOffset = value; callPropertyChanged("verticalOffset",this)
  }
  def horizontalOffset_=(value:Float):Unit = {
    this._horizontalOffset = value; callPropertyChanged("horizontalOffset",this)
  }
  def viewportWidth_=(value:Float):Unit = {
    this._viewportWidth = value; callPropertyChanged("viewportWidth",this)
  }
  def viewportHeight_=(value: Float): Unit = {
    this._viewportHeight = value; callPropertyChanged("viewportHeight", this)
  }
  def barWidth_=(value:Float):Unit = {
    this._barWidth = value;callPropertyChanged("barWidth",this)
  }
  def barHeight_=(value:Float):Unit = {
    this._barHeight = value;callPropertyChanged("barHeight",this)
  }
  //endregion

  override def Enter(): Unit = {
    super.Enter()
    this.routeEventController.addEvent(ScrollContentPresenter.ContentChanged,onContentChanged)
  }

  def hookContentPresenter(scrollContent: ScrollContentPresenter):Unit = {
    this.scrollContent = Some(scrollContent)
    this.content match
      case contentElement: UIElement =>
        scrollContent.addChild(contentElement)
      case _ =>
  }

  override def onPropertyChanged(propertyName: String): Unit = {

  }

  protected def onContentChanged(args:RouteEventArgs):Unit = {
    args.handled = true
    val sizeArgs:ScrollSizeChangedEvent = args.asInstanceOf[ScrollSizeChangedEvent]
    var hasDirty = false
    if(sizeArgs.isViewport) {
      if (sizeArgs.width != this._viewportWidth) { this.viewportWidth = sizeArgs.width; hasDirty = true }
      if (sizeArgs.height != this._viewportHeight) { this.viewportHeight = sizeArgs.height; hasDirty = true }
    } else {
      if (sizeArgs.width != this._scrollableWidth) {this.scrollableWidth = sizeArgs.width; hasDirty = true }
      if (sizeArgs.height != this._scrollableHeight) { this.scrollableHeight = sizeArgs.height;  hasDirty = true }
    }

    if(hasDirty) {
      this.barWidth = (this.viewportWidth / this.scrollableWidth) * this.viewportWidth
      this.barHeight = (this.viewportHeight / this.scrollableHeight) * this.viewportHeight
      println(s"barW:${this.barWidth} barH:${this.barHeight}")
    }
  }

  override def Exit(): Unit = {
    super.Exit()
    this.routeEventController.removeEvent(ScrollContentPresenter.ContentChanged)
  }

}