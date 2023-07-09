package ui.controls
import core.reflect.*;

class ScrollViewer extends ContentControl derives ReflectType {
  protected var _scrollableHeight:Float = 0f
  protected var _scrollableWidth:Float = 0f
  protected var _horizontalOffset:Float = 0f
  protected var _verticalOffset:Float = 0f
  protected var _viewportWidth:Float = 0f
  protected var _viewportHeight:Float = 0f

  protected var scrollInfo:Option[IScrollInfo] = None
  private   var xPositionISI:Float = 0
  def setScrollInfo(info:Option[IScrollInfo]):Unit = {
    this.scrollInfo = info;
  }

  //region Setter

  def scrollableHeight:Float = this._scrollableHeight
  def scrollableWidth:Float = this._scrollableWidth
  def horizontalOffset:Float = this._horizontalOffset
  def verticalOffset:Float = this._verticalOffset
  def viewportWidth:Float = this._viewportWidth
  def viewportHeight:Float = this._viewportHeight
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
  //endregion

  override def OnEnter(): Unit = {
    super.OnEnter()
  }
}