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

因此除了将包设置成简单的String类型之外。@ComponentScan还提供了另一种方法，那就是将期指定为包中所包含的类或接口：
```
@Configuration
@ComponentScan(basePackageClasses = CDPlayer.class)
public class AppConfig {
}
```
我们通过将类信息赋值给basePackageClasses，从而@ComponentScan将会以这些类所在的包作为基础包进行扫描。
#### 2.2.4 通过bean添加注解实现自动装配
简单来说，自动装配就是让Spring自动满足bean依赖的一种方法，在满足依赖的过程中，会在Spring应用上下文中寻找匹配某个bean需求的其他bean。我们通过@AutoWired注解来进行自动装配。

举个例子，我们可以在CDPlayer类的构造器中添加@AutoWired注解，这表明当Spring创建CDPlayer的bean时，会通过这个构造器来进行实例化，并传入一个CompactDisk类型的bean。
```
@Component
public class CDPlayer {

    public CDPlayer() {
        System.out.println("CDPlayer无参构造函数");
    }

    private CompactDisk cd;

    @Autowired
    public CDPlayer(CompactDisk cd) {
        this.cd = cd;
        System.out.println("CDPlayer有参构造函数");
    }

    public void play(){
        cd.play();
    }
}
```
@Autowired注解不仅可以用在构造器上，还能用在属性的Setter方法上。例如，CDPlayer有一个setCompactDisk方法。那么可以采用@Autowired注解形式进行自动装配。
```
@Autowired
public void setCompactDisk(CompactDisk cd){
  this.cd = cd;
}
```
不管是构造器，Setter方法还是其他方法。Spring都会尝试满足方法参数上锁声明的依赖。加入有且只有一个bean满足依赖需求时，那么这个bean将会被装载进来。

如果没有相匹配的bean时，那么在应用上下文创建的时候，Spring会抛出一个异常。为避免异常的出现，你可以将@Autowired的required属性设置为false：
```
@Autowired(required = false)
public CDPlayer(CompactDisk cd) {
    this.cd = cd;
    System.out.println("CDPlayer有参构造函数");
}
```
当然require属性为false时，Spring会尝试执行装配，但是如果没有相匹配的bean时，Spring将会让这个bean处于未装配的状态。因此你需要谨慎对待设置require为false的bean，以防报空错误。

除此之外，在有多个bean均能满足依赖关系时，@Autowired也将无法正常自动装配。就自动装配的歧义性将在第三部分讨论。

### 2.3 通过Java代码装配bean
尽管通过组件扫描和自动化配置是非常简单省事的操作方式，但是有时候自动化配置行不通，因此需要明确配置Spring。比如说，你想要将第三方库中的组件装配到你的应用中，由于你无法在类库中添加@Component和@Autowired注解，自动化装配方案将不再可行。

该情况下我们必须采用显示装配的方式。再进行显示配置的时候，有两种可选方案：Java和XML。

在进行显示配置时，JavaConfig时更好的方案，因为它更为强大，类型安全，且对重构友好。

#### 2.3.1 创建配置类
我们曾使用过JavaConfig，让我们再看一次。
```
@Configuration
public class AppConfig {
}
```
创建JavaConfig类的关键在于为其添加@Configuration注解，@Configuration注解表明该类为配置类，该类应该包含在Spring应用上下文中如何创建bean的细节。

我们先前采用组件扫描的方式进行bean的创建。在本节中我们使用显示配置，所以将不再需要@ComponentScan注解。

#### 2.3.2声明简单的bean
要在JavaConfig中声明bean，我们需要编写一个方法，这个方法会创建所需类型的实例，然后给这个方法添加@Bean注解。
```
@Bean
public CompactDisk diskOne(){
  return new DiskOne();
}
```
@Bean注解会告诉Spring这个方法将会返回一个对象，该对象要注册为Spring应用上下文中的bean。方法体中包含了最终产生bean实例的逻辑。

默认情况下bean的ID和方法名一致，当然你也可以通过设置@Bean的参数来更改其ID。
```
@Bean(name = "myDisk")
public CompactDisk diskOne(){
  return new DiskOne();
}
```

#### 2.3.3 借助JavaConfig实现注入
我们可以看到再2.3.2中，我们声明的bean没有其他依赖，所以其生成方式也十分简单。如果我们声明CDPlayer，它依赖于CompactDisk，拿我们就需要将二者装配在一起。

在JavaConfig中装配bean的最简单方式就是引用创建bean的方法。
```
@Bean
public CDPlayer cdPlayer(){
  return new CDPlayer(diskOne());
}
```
cdPlayer()的方法体与diskOne有些区别，它没有使用默认的构造器来构建实例，而是调用了需要传入CompactDisk对象的构造器来创建CDPlayer实例。

看起来，CompactDisk是通过调用diskOne()得到的，但实际并非如此/diskOne()方法上添加了@Bean注解，Spring会拦截所有对它的调用，并确保直接返回该方法所创建的bean，而不是每次都对其进行实际的调用。

例如，你引入了一个其他的CDPlayer的bean，内容和之前的完全一致：
```
@Bean
public CDPlayer cdPlayer(){
  return new CDPlayer(diskOne());
}

@Bean
public CDPlayer anotherCDPlayer(){
  return new CDPlayer(diskOne());
}
```
假如对diskOne()的调用就像其他的Java方法一样，那么每个CDPlayer实例都会有一个自己特有的DiskOne实例。但是Spring中的bean都是单例的，我们没有必要为第二个CDPlayerbean创建一个完全相同的DiskOne实例。所以Spring会拦截对diskOne()的调用，并确保返回的是Spring所创建的bean，也就是Spring本身在调用diskOne()时所创建的CompactDisk的bean。因此，两个CDPlayer的bean会得到相同的DiskOne实例。

我们可以将上述操作以一种更简单的方式来理解
```
@Bean
public CDPlayer cdPlayer(CompactDisk compactDisk){
  return new CDPlayer(compactDisk);
}
```

在这里，cdPlayer()方法请求一个CompactDisk作为参数。当Spring调用cdPlayer()创建CDPlayerbean的时候，它会自动装配一个CompactDisk到配置方法之中。借助这种技术，cdPlayer()方法也能够将CompactDisk注入到CDPlayer的构造器中，而且不用明确引用CompactDisk的@Bean方法。

带有@Bean注解的方法可以采用任何必要的Java功能来产生bean实例，而非仅局限于构造器和Setter方法。

### 2.4 通过XML装配bean
该部分暂不管。。。。
### 2.5 导入和混合配置
由于涉及到XML，暂不管+1

## 三、高级装配
在上一章我们学习了一些最为核心的装配技术。本章节中，我们将会更深入地介绍一些这样的高级技术。
### 3.1 环境与profile
在开发软件的时候，将应用程序从一个环境迁移到另外一个环境是一项巨大的挑战。开发阶段的某些环境相关做法可能并不适合迁移到生产环境中，甚至迁移过去也无法正常工作。其典型例子就是数据库配置、加密算法以及与外部系统的集成。

#### 3.1.1 配置profile bean
#### 3.1.2 激活profile
### 3.2 条件化的bean
### 3.3 处理自动装配的歧义性
#### 3.3.1 标示首选的bean
#### 3.3.2 限定自动装配的bean
### 3.4 bean的作用域
#### 3.4.1 使用会话和请求作用域
#### 3.4.2 在XML中声明作用域代理
### 3.5 运行时注入
#### 3.5.1 注入外部的值
#### 3.5.2 使用Spring表达式语言进行装配
### 3.6 小结

## 第四章 面向切面的Spring
在软件开发中，散布于应用中多处的功能被称为横切关注点（cross-cutting concern）。通常来讲，这些横切关注点从概念上是与应用的业务逻辑相分离的（但是往往胡直接嵌入到应用的业务逻辑之中）。把这些横切关注点与业务逻辑相分离正是面向切面编程（AOP）索要解决的问题。

### 4.1 什么是面向切面编程
如前所述，切面能帮助我们模块化横切关注点。简而言之，横切关注点可以被描述为影响应用多处的功能。例如，安全就是一个横切关注点，应用中的许多方法都会涉及到安全规则。

在程序中，每个模块的核心功能都是为特定业务领域提供服务，但是这些模块都需要类似的辅助功能，例如安全和事务管理。

如果要重用通用功能的话，最常见的面向对象技术是继承或委托。但是，如果在整个应用中都使用相同的基类，继承往往会导致一个脆弱的对象体系；而使用委托可能需要对委托对象进行复杂的调用。

切面提供了取代继承和委托的另一种可选方案，而且在很多场景下更清晰简洁。在使用面向切面编程时，我们仍然在一个地方定义通用功能，但是可以通过声明的方式定义这个功能要以何种方式在何处应用，而无需修改受影响的类。横切关注点可以被模块化为特殊的类，这些类被称为切面（aspect）。

#### 4.1.1 定义AOP术语
与大多数技术一样，AOP已经形成了自己的术语。常用术语有通知（advice），切点（pointcut）和连接点（joint point）。接下来我们以电力公司运作为例来描述AOP运作。

通知（Advice）



