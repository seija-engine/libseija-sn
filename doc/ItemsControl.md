## ItemsControl实现梳理  

### 数据源 
支持两种数据源，数据源都实现了`IList[T]`接口, `T`可以是任意类型   
1. `Items` 为直接通过Xaml填充的子元素列表。  
1. `itemsSource`是可以通过数据绑定的任何的数据列表, 同时如果数据容器实现了`INotifyCollectionChanged`,`ItemsControl`可以根据数据容器的事件精确的动态修改显示的元素列表。  

### 基础控件结构  
```xml
<ItemsControl>

    <ItemsControl.template>
        <ControlTemplate>
            <Panel>
                <Image sprite="default.white" color="#cccccc" />
                <ItemsPresenter  />
            </Panel>
        </ControlTemplate>
    </ItemsControl.template>

    <ItemsControl.ItemsPanel>
        <ItemsPanelTemplate>
            <StackPanel />
        </ItemsPanelTemplate>
    </ItemsControl.ItemsPanel>

    <ItemsControl.itemTemplate>
      <DataTemplate>
        <Panel isCanvas="true" height="30" width="730">
          <Image sprite="default.white" color="#111199" height="30"/>
          <Text color="#ffffff" text="{Binding Data this}"/>
        </Panel>
      </DataTemplate>
    </ItemsControl.itemTemplate>

</ItemsControl>
```
1. 首先`ItemsControl`是`Control`所以他有一个template决定了自己的外观.    
2. `ItemsPresenter`类似于`ContentPresenter`他决定了列表添加到哪个位置.   
3.  `ItemsPanel`用来设置元素列表的父`Panel`元素.  
4. `itemTemplate`则是数据源类型对应的DataTemplate.  

### 代码实现细节   
#### `ItemsControl`实现细节  
##### `itemCollection:ItemCollection`  
为数据源的整合，会派发出数据源变化的事件.  
##### `IGeneratorHost`.
`ItemsControl`实现了`IGeneratorHost`接口，这个接口表示元素生成的`Host`  
1. 其可以查询到`ItemCollection`  
2. `GetContainerForItem`  
每个`DataTemplate`实例化出的`UIElement`可以有一个父容器例如`ListBox`的Item都是`ListBoxItem`这些Item容器可以承载一些特殊功能。 `GetContainerForItem`就是用来获取父容器的接口  
##### `ItemContainerGenerator`  
元素的生成器,会传入`IGeneratorHost`, 赋值生成具体的元素。  

#### `ItemsPresenter`实现细节  
`ItemsPresenter`默认读取ItemsControl的`ItemsPanel`作为自己的`Template`。    
`Template`类型为`ItemsPanelTemplate`会默认给`Panel`设置`IsItemsHost`为`true`  

#### `ItemsPanel`  
`ItemsPanel`必须只有一个元素并且父类型为`Panel`。`IsItemsHost`会被设置为`true`。  
当`Panel`的`IsItemsHost`为`true`时会自动连接到`Generator`(通过`TemplateParent`).  