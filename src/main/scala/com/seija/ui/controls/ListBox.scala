package com.seija.ui.controls
import com.seija.core.reflect.ReflectType;

class ListBox extends Selector derives ReflectType {
    override def GetContainerForItemOverride(): UIElement = new ListBoxItem()
}