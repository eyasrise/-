# 手写spring

### 基于spring注解动态加载bean对象，并实现单例，多例
逻辑

* 扫描包路径得到类路径List
* 通过路径装载bean对象
* 通过bean对象构建Component注解的beanDefinition(包括单例，多例，懒加载)
* 并把beanDefinition以键值对方式到map中
* 遍历单例map，构建单例池。

