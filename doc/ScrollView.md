## ScrollViewer
2. `ScrollViewer`是一个`ContentControl`,`ScrollContentPresenter`专门用来放置内容.
2. `ScrollViewer`分为物理滚动和逻辑滚动，逻辑滚动需要内容控件里的Panel提供滚动信息，实现接口`IScrollInfo`。  
3. `ScrollContentPresenter`是视口，`ScrollContentPresenter`的第一个Children是可滚动区域。



### Thumb
1. 单纯可以响应拖动事件的控件

### Track
1. 表示有一个`Thumb`在一个横向或者纵向轨道上移动的控件。  
2. 有属性`MinValue`,`MaxValue`,`Value`,`ThumbSize`,`TrackLength`。
3. `Track`会根据以上属性变化更新`Thumb`的位置和大小

### ScrollBar
### Slider
