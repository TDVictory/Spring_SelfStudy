# Spring_SelfStudy
## 一、核心概念
### IoC
- Inversion of Control
- 控制反转
### DI
- Dependency Injection
- 依赖注入
### AOP
- Aspect Oriented Programming
- 面向切面编程
## 二、装配Bean
### 2.1Spring配置的可选方案
- 在XML中进行显式配置
- 在Java中进行显示配置
- 隐式的bean发现机制和自动装配
### 2.2自动化装配bean
  Spring从两个角度上来实现自动化装配：
  - 组建扫描（component scanning）：Spring会自动发现应用上下文中创建的bean。
  - 自动装配（autowiring）：Spring自动满足bean之间的依赖。
#### 2.2.1 创建可被发现的bean
通过在类上注解@Component的方式，表明该类位组建类，并告知Spring要为这个类创建bean。
```
@Component
public class CompactDisk {
    public CompactDisk() {
        System.out.println("CompactDisk无参构造函数");
    }

    public void play(){
        System.out.println("播放音乐");
    }
}
```
不过组件扫描默认不启用。我们需要显式配置一下Spring，从而命令它去寻找带有@Component注解的类，并为其创建bean。
```
@Configuration
@ComponentScan
public class AppConfig {
}
```
类AppConfig通过Java代码定义了Spring的装配规则。

如果没有其他配置的话，@ComponentScan默认会扫描与配置类相同的包。Spring会扫描包含配置类的包以及这个包下的所有子包，查找带有@Component注解的类。
#### 2.2.2 为组件扫描的bean命名
Spring应用上下文中所有bean都会给定一个ID，如果没有明确设置ID，Spring会根据类名为其指定一个ID。该ID为讲首字母小写的该类名（CompactDisk的ID为compactDisk）。

如果需要设置用户自定义ID，则修改@Component注解如下：
```
@Component("disk")
public class CompactDisk {
    ......
}
```

这样，CompactDisk的ID为disk。
#### 2.2.3设置组件扫描的基础包
除了给@Component设置参数。@ComponentScan也可以设置参数属性。因为在不设置参数的情况下，它会以配置类所在的包作为基础包（base package）来扫描组件。这也就意味着你不能扫描到其他包内的组件。

为了指定不同的基础包，你所需要做的就是在@ComponentScan的value属性中指定包名：
```
@Configuration
@ComponentScan("SoundSystem")
public class AppConfig {
}
```
如果你想更加清晰地表明你设置的是基础包，那么你可以通过basePackages属性进行配置：
```
@Configuration
@ComponentScan(basePackages = "SoundSystem")
public class AppConfig {
}
```
该方法可以通过传入一个数组来指定多个包：
```
@Configuration
@ComponentScan(basePackages = {"SoundSystem", "video"})
public class AppConfig {
}
```
我们可以看到该属性是String类型的，因此这种方法虽然可行但是是类型不安全（not type-safe）的。如果你重构代码的话，指定的基础包就会出现错误。

因此除了将包设置成简单的String类型之外。@ComponentScan还提供了另一种犯法，那就是将期指定为包中所包含的类或接口：
```
@Configuration
@ComponentScan(basePackageClasses = CDPlayer.class)
public class AppConfig {
}
```
我们通过将类信息赋值给basePackageClasses，从而@ComponentScan将会以这些类所在的包作为基础包进行扫描。
#### 2.2.4 通过bean添加注解实现自动装配
简单来说，自动装配就是让Spring自动满足bean依赖的一种方法，在满足依赖的过程中，会在Spring应用上下文中寻找匹配某个bean需求的其他bean。我们通过@AutoWired注解来进行自动装配。

