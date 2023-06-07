1.  `ContentPresenter`的`Content`, `ContentTemplate`,`ContentTemplateSelector`会默认继承`ContentControl`的属性。  
手动指定这些属性后默认值会被替换。  
2. 如果`Content`是UI元素并且没有指定的`ContentTemplate`会直接显示该元素。  
3. 如果显示设置了`ContentTemplate`属性，` ContentPresenter`会把`DataTemplate`应用到`Content`上。  
4. 如果Resources中有`DataTemplate`的应用类型等于`Content`的类型，`ContentPresenter`也会把`DataTemplate`应用到`Content`上。  
5. 非常重要的一点是`ContentPresenter`会默认把`Content`设置给`DataContext`。 
6.  `ContentPresenter`可以单独作为控件使用。  