package com.seija.ui.controls
import com.seija.ui.ContentProperty
import com.seija.core.reflect.*

@ContentProperty("content")
class ContentControl extends Control derives ReflectType {
    var content:Any = _
    var contentTemplate:Option[DataTemplate] = None
}