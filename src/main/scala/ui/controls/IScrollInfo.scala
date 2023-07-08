package ui.controls

trait IScrollInfo {
    def SetHorizontalOffset(offset:Float):Unit;
    def SetVerticalOffset(offset:Float):Unit;

    var scrollViewer:Option[ScrollViewer];
}