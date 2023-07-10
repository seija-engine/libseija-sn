package ui.controls
import core.UpdateMgr
import core.reflect.*
import ui.UIModule
import ui.core.{Rect2D, Rect2DBuilder};

class ScrollViewer extends ContentControl derives ReflectType {
  protected var _scrollableHeight:Float = 0f
  protected var _scrollableWidth:Float = 0f
  protected var _horizontalOffset:Float = 0f
  protected var _verticalOffset:Float = 0f
  protected var _viewportWidth:Float = 0f
  protected var _viewportHeight:Float = 0f

  private var scrollContent:Option[ScrollContentPresenter] = None

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

  override def Enter(): Unit = {
    super.Enter()
    UIModule.addPostLayoutCall(this.scrollContent.get.childEntity.get,this.OnPostScrollLayout);
  }

  def hookContentPresenter(scrollContent: ScrollContentPresenter):Unit = {
    this.scrollContent = Some(scrollContent)
    this.content match
      case contentElement: UIElement =>
        scrollContent.addChild(contentElement);
        UIModule.addPostLayoutCall(scrollContent.getEntity().get, this.OnPostLayout)
      case _ =>
  }

  private def OnPostLayout():Unit = {
    this.scrollContent.flatMap(_.getEntity()).foreach { e =>
      val rect2d = e.get[Rect2D]()
      this.viewportWidth = rect2d._1;
      this.viewportHeight = rect2d._2;
    }
  }

  private def OnPostScrollLayout():Unit = {
    this.scrollContent.flatMap(_.childEntity).foreach { e =>
      val rect2d = e.get[Rect2D]()
      this.scrollableWidth = rect2d._1;
      this.scrollableHeight = rect2d._2;
    }
  }

  override def onPropertyChanged(propertyName: String): Unit = {

  }
  override def Exit(): Unit = {
    this.scrollContent.flatMap(_.getEntity()).foreach(UIModule.removePostLayoutCall)
    this.scrollContent.flatMap(_.childEntity).foreach(UIModule.removePostLayoutCall)
    super.Exit()
  }
}