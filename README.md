# libseija-sn  
seija的逻辑上层实现.  

## 开发路线

### 设计基础的组件和实体机制 
1. 设计Rust内部ECS的Entity和Component FFI机制  
2. 设计脚本层自定义组件机制    

### 将基础常用接口包装进脚本层  
1. 包装Transform相关  
2. 包装Input相关  
3. 将Asset相关 

### 实现WPF Like的UI框架  
1. 用宏实现任意类的反射支持  
反射是Xaml数据反序列化和数据绑定的基础中的基础，优先实现。  
2. 实现一套动态类型转换机制和接口
