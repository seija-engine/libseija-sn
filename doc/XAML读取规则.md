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

## 反射类  
1. 反射类，先通过Xml的Tag名根据命名空间规则找个这个类的反射信息，然后用反射信息创建类    
```xml
<Window>
    <Button  />
</Window>
```
例如上面，会反射创建ui.controls.Button类。  

### 设置attr String类型的属性  
根据反射信息获取对应字段的类型，然后查找这个类型对Into[String,T]的实现。  
```xml
<Window>
    <Button width="100" height="200" color="#ff0000" />
</Window>
```  

### 设置xml元素类型的属性,
子元素中`Tag.PropName`是特殊的Xml类型属性，这个Xml子元素分成三种情况。  
  1. 如果这个Xml子元素只有字符串的innerText，那么等同于attr String的读取。  
  2. 如果这个Xml子元素只有一个子元素，将子元素反射为类，然后将这个类赋值给这个属性。
  3. 如果这个Xml子元素有多个元素，那么这个属性必须实现Buffer接口，然后会将所有子元素反射为类，依次添加进指个接口。  
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

### 设置类的正文属性  
会有一个`ContentProperty("ctx")`注解，注解可以指定这个类的正文属性。读正文属性的时候会先排除当前类的xml元素属性,然后这个正文属性也分为三种情况.    
1. 如果只有一个innerText正文，使用attr String的读取，会提供一个Into[String,Any]的转换实现，所以如果正文是个Any会直接读入这个String。  
2. 只有一个子元素，反射这个类赋值给正文属性  
3. 有多个元素，正文属性必须实现Buffer接口，然后将所有子元素反射为类，依次添加进指个接口。 
```xml
<StackPanel>
<Button> OK </Button>
<Button> <Image sprite="white" /> </Button>
</StackPanel>
```

### 映射属性时的Option问题  
在`DynTypeConv`中已经写了特殊规则，所有T类型，转换为Option[T]都会直接自动处理。  

### 映射属性时的Any问题  
