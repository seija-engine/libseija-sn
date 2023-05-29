# 概述  
XAML的读取是完全依赖于反射的，Scala的整个命名空间都能映射给XAML。所以XAML的读取就是反射任意Scala类的读取。  
当然同时由于XAML的结构并不是和Scala的结构一一对应的，所以XAML的读取有一些特殊规则，下面展开描述。   
下面展开描述中的XAML是经过转换的我的实现的XAML，会和微软的XAML有一些差异，但是不影响理解。  


## 映射命名空间  
映射分为单一命名空间映射，指定映射。 
1. 单一命名空间映射如下所示，在顶层元素定义命名空间映射例如下面这样,将scala的`System`映射给了`sys`命名空间。
```xml
<Window xmlns:sys="System" >
</Window>
```  
2. 指定映射可以将多个Scala命名空间映射给一个XAML命名空间。一般应用于默认命名空间。可以通过一个xml文件单独指定。  
```xml
<NS name="default">
   <Import>ui.controls.*</Import>
   <Import>ui.resources.*</Import>
   <Import>ui.core.Thickness</Import>
</NS>
```  
3. 默认命名空间是不需要加前缀的。  

### 反射类  
1. 反射类，先通过Xml的Tag名根据命名空间规则找个这个类的反射信息，然后用反射信息创建类    
```xml
<Window>
    <Button  />
</Window>
```
例如上面，会反射创建ui.controls.Button类。  

2. 设置attr类型的属性, 根据反射信息获取对应字段的类型，然后查找这个类型对Into[String,T]的实现。  
```xml
<Window>
    <Button width="100" height="200" color="#ff0000" />
</Window>
```  

3. 设置xml元素类型的属性  
```xml
<Window>
    <Button width="100" height="200" color="#ff0000">
      <Button.template>
        <ControlTemplate>
        </ControlTemplate>     
      </Button.template>
    </Button>
</Window>
```
### 反射XML的Context给类
