package ui.controls
import core.UpdateMgr
import core.reflect.*
import ui.UIModule
import ui.core.{Orientation, Rect2D, Rect2DBuilder}
import ui.event.RouteEventArgs

class ScrollViewer extends ContentControl derives ReflectType {
  protected var _scrollableHeight:Float = 0f
  protected var _scrollableWidth:Float = 0f
  protected var _horizontalOffset:Float = 0f
  protected var _verticalOffset:Float = 0f
  protected var _viewportWidth:Float = 0f
  protected var _viewportHeight:Float = 0f
  protected var _barWidth:Float = 0f
  protected var _barHeight:Float = 0f
  protected var _showHorBar:Boolean = false
  protected var _showVerBar:Boolean = false

  private var scrollContent:Option[ScrollContentPresenter] = None
  private var contentWidth:Float = 0
  protected var contentHeight:Float = 0

  //region Setter

  def scrollableHeight:Float = this._scrollableHeight
  def scrollableWidth:Float = this._scrollableWidth
  def horizontalOffset:Float = this._horizontalOffset
  def verticalOffset:Float = this._verticalOffset
  def viewportWidth:Float = this._viewportWidth
  def viewportHeight:Float = this._viewportHeight
  def barWidth: Float = this._barWidth
  def barHeight: Float = this._barHeight
  def showHorBar:Boolean = this._showHorBar
  def showVerBar:Boolean = this._showVerBar
  def showHorBar_=(value:Boolean):Unit = if(value != this._showHorBar) {this._showHorBar = value; callPropertyChanged("showHorBar",this)  }
  def showVerBar_=(value:Boolean):Unit = if(value != this._showVerBar) {this._showVerBar = value; callPropertyChanged("showVerBar",this)  }
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
    this.routeEventController.addEvent(ScrollBar.ScrollValueChanged,this.onScrollValueChanged)

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
      if (sizeArgs.width != this.contentWidth) {this.contentWidth = sizeArgs.width; hasDirty = true }
      if (sizeArgs.height != this.contentHeight) { this.contentHeight = sizeArgs.height;  hasDirty = true }
    }
    if(hasDirty) {
      this.scrollableWidth = this.contentWidth - this._viewportWidth
      this.scrollableHeight = this.contentHeight - this._viewportHeight
      if(this.scrollableHeight > 0) {
        this.barHeight = (this.viewportHeight / this.contentHeight) * this.viewportHeight
        this.showVerBar = true;
        if(this._verticalOffset > this._scrollableHeight) {
          this.verticalOffset = this._scrollableHeight;
        }
        this.scrollContent.foreach(_.setVerticalOffset(this.verticalOffset))
      } else {
        this.showVerBar = false;
      }

      if(this.scrollableWidth > 0) {
         this.barWidth = (this.viewportWidth / this.contentWidth) * this.viewportWidth
      }
      
    }
  }

  protected def onScrollValueChanged(args:RouteEventArgs):Unit = {
    args.handled = true
    val scrollArgs = args.asInstanceOf[ScrollValueChangedEventArgs]
    scrollArgs.ori match {
      case Orientation.Horizontal => {
        this.horizontalOffset = scrollArgs.value
        
      }
      case Orientation.Vertical => {
        this.verticalOffset = scrollArgs.value
        this.scrollContent.foreach(_.setVerticalOffset(this.verticalOffset))
      }
    }
  }

  override def Exit(): Unit = {
    super.Exit()
    this.routeEventController.removeEvent(ScrollContentPresenter.ContentChanged)
  }

}