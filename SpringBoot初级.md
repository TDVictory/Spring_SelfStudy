# Spring Boot入门

## Spring Boot 简介
- 简化Spring应用开发的一个框架；
- 整个Spring技术栈的一个大整合；
- J2EE开发的一站式解决方案；

## 微服务
微服务：架构风格（服务微化）

一个应用应该是一组小型服务；可以通过HTTP的方式来进行互通

每一个功能元素最终都是一个可独立替换和独立升级的软件单元；

# 一、基础探究
## 1.1 POM文件
### 1.1.1 父项目
```xml
<!-- 父级依赖 -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.6.RELEASE</version>
    </parent>
```
他的父项目
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-dependencies</artifactId>
    <version>2.1.6.RELEASE</version>
    <relativePath>../../spring-boot-dependencies</relativePath>
</parent>
```
下面有个属性，定义了对应的版本号
```xml
<properties>
    <activemq.version>5.15.9</activemq.version>
    <antlr2.version>2.7.7</antlr2.version>
    <appengine-sdk.version>1.9.75</appengine-sdk.version>
    <artemis.version>2.6.4</artemis.version>
    <aspectj.version>1.9.4</aspectj.version>
    <assertj.version>3.11.1</assertj.version>
    <atomikos.version>4.0.6</atomikos.version>
    <bitronix.version>2.1.4</bitronix.version>
    <build-helper-maven-plugin.version>3.0.0</build-helper-maven-plugin.version>
    <byte-buddy.version>1.9.13</byte-buddy.version>
    <caffeine.version>2.6.2</caffeine.version>
    <cassandra-driver.version>3.6.0</cassandra-driver.version>
    <classmate.version>1.4.0</classmate.version>
    ......
```
他来真正管理Spring Boot应用里面的所有依赖版本；是Spring Boot的版本仲裁中心；

### 1.1.2 启动器
```xml
<!-- 使用SpringMVC和Spring的jar包 -->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```
**spring-boot-starter-web**
- spring-boot-starter:spring-boot场景启动器，帮我们导入了web模块正常运行所依赖的组件；

Spring Boot将所有的功能场景都抽取出来，做成一个个的starter（启动器），只需要在项目里面引入这些starter，相关场景的所有依赖都会导入进来。要用什么功能就导入什么场景的启动器。

## 1.2 主程序类，主入口类
```java
@SpringBootApplication

public class HelloWorldMainApplication {

    public static void main(String[] args) {

        //启动Spring
        SpringApplication.run(HelloWorldMainApplication.class,args);
    }
}
```
**@SpringBootApplication**: 说明这个类是SpringBoot的主配置类，SpringBoot就应该运行这个类的main方法来启动应用

SpringBootApplication注解详细信息：
```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(
    excludeFilters = {@Filter(
    type = FilterType.CUSTOM,
    classes = {TypeExcludeFilter.class}
), @Filter(
    type = FilterType.CUSTOM,
    classes = {AutoConfigurationExcludeFilter.class}
)}
)
public @interface SpringBootApplication {
```
- **@SpringBootConfiguration**：Spring Boot的配置类；标注在某个类上，表示这是一个Spring Boot的配置类；
  - **@Configuration**：配置类上来标注这个注解；配置类也是容器中的一个组件；即@Component
```java
@Configuration
public @interface SpringBootConfiguration {
}
```
- **EnableAutoConfiguration**：开启自动配置功能；以前我们需要配置的东西，Spring Boot帮我们自动配置；
  - **@AutoConfigurationPackage**：自动配置包，基于Spring底层注解 **@Import**，给容器中导入组件。
  
  将主配置类（@SpringBootApplication标注的类）的所在包及下面所有子包里面的所有组件扫描到Spring容器；
  - **@Import**({AutoConfigurationImportSelector.class})：将所有需要导入的组件以全类名的方式返回；这些组件就会被添加到容器中。
  
  会给容器中导入非常多的自动配置类。有了自动配置类，免去了我们手动编写配置注入功能组件的工作。
```java
@Inherited
@AutoConfigurationPackage
@Import({AutoConfigurationImportSelector.class})
public @interface EnableAutoConfiguration {
```

Spring Boot在启动的时候从类路径下的META-INF/spring.factorys中获取的EnableAutoConfiguration指定的值；

将这些值作为自动配置类导入到容器中，自动配置就生效了。

J2EE的整体解决方案：

org\springframework\boot\spring-boot-autoconfigure\2.0.1.RELEASE\spring-boot-autoconfigure-2.1.6.RELEASE.jar

## 1.3 使用Spring Initializer创建一个快速向导
1.IDE支持使用Spring Initializer

自己选择需要的组件:例如web

默认生成的SpringBoot项目

主程序已经生成好了，我们只需要完成我们的逻辑

resources文件夹目录结构
- static:保存所有的静态文件；js css images
- templates:保存所有的模板页面；（Spring Boot默认jar包使用嵌入式的Tomcat,默认不支持JSP）；可以使用模板引擎（freemarker.thymeleaf）;
- application.properties:Spring Boot的默认配置，例如 server.port=9000

# 二、配置文件
## 2.1 配置文件
SpringBoot使用一个全局配置文件，配置文件名是固定的；
- application.properties
- application.yml

配置文件的作用：修改SpringBoot自动配置的默认值（所有配置SpringBoot在底层会帮我们以默认值配置完成）

YAML（YAML AIN'T Markup Language）
- 是一个标记语言
- 又不是一个标记语言

标记语言：
- 以前的配置文件；大多数使用的是 xxx.xml文件；
- 以**数据为中心**，比json、xml等更适合做配置文件

YAML：配置例子
```yml
server:
    port:8081
```
XML:
```xml
<server>
    <port>8081</port>
</server>
```

## 2.2 YAML语法：
### 2.2.1 基本语法
k:（空格）v:表示一对键值对（空格必须有）

以空格的缩进来控制层级关系；只要是左对齐的一列数据，都是同一个层级
```yaml
server:
    port:8081
    path:/hello
```

### 2.2.2 值的写法
#### 字面量：普通的值（数值，字符串，布尔）
k: v:字面直接来写；

字符串默认不用加上单引号或者双引号

"":双引号 不会转义字符串里的特殊字符；特殊字符会作为本身想要表示的意思

```name:"zhangsan\n lisi"``` 输出：```zhangsan换行 lisi```

'':单引号 会转义特殊字符，特殊字符最终只是一个普通的字符串数据

```name:'zhangsan\n lisi'``` 输出：```zhangsan\n lisi```
#### 对象：Map（属性和值）（键值对）
k :v ：在下一行来写对象的属性和值的关系；注意空格控制缩进

对象还是k:v的方式
```yml
frends:
    lastName: zhangsan
    age: 20
```
行内写法
```yaml
friends: {lastName: zhangsan,age: 18} 
```
#### 数组（List，Set）：
用-表示数组中的一个元素
```yaml
pets:
 ‐ cat
 ‐ dog
 ‐ pig 
```
行内写法
```yaml
pets: [cat,dog,pig] 
```

### 2.2.3 配置文件值注入
#### 1、@ConfigurationProperties
在使用@ConfigurationProperties之前，我们需要导入配置文件处理器，以后编写配置就有提示了
```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring‐boot‐configuration‐processor</artifactId>
	<optional>true</optional>
</dependency> 
```

1、**application.yml 配置文件**
```yml
person:
  last-name: wanghuahua
  age: 18
  boss: false
  birth: 2017/12/12
  maps: {k1: v1,k2: 12}
  lists:
   - lisi
   - zhaoliu
  dog:
    name: wangwang
    age: 2
```

2、**application.properties** 配置文件

我们需要将idea配置文件编码格式转为utf-8, 因为properties 默认GBK，如果不转换的话会产生乱码。
```java
person.age=12
person.boss=false
person.last-name=张三
person.maps.k1=v1
person.maps.k2=v2
person.lists=a,b,c
person.dog.name=wanghuahu
person.dog.age=15
```
#### 2、@Value注解

更改javaBean中的注解
```java
@Component
public class Person {
    /**
     * <bean class="Person">
     *     <property name="lastName" value="字面量/${key}从环境变量/#{spEL}"></property>
     * </bean>
     */
    @Value("${person.last-name}")
    private String lastName;
    @Value("#{11*2}")
    private Integer age;
    @Value("true")
    private Boolean boss;
```

 使用场景分析

- 如果说，我们只是在某个业务逻辑中获取一下配置文件的某一项值，使用@Value；
- 如果专门编写了一个javaBean和配置文件进行映射，我们直接使用@ConfigurationProperties

#### 3、@PropertySource注解
@**PropertySource**

作用：加载指定的properties配置文件

新建一个person.properties文件
```java
person.age=12
person.boss=false
person.last-name=李四
person.maps.k1=v1
person.maps.k2=v2
person.lists=a,b,c
person.dog.name=wanghuahu
person.dog.age=15
```
在javaBean中加入@PropertySource注解
```java
@PropertySource(value = {"classpath:person.properties"})
@Component
@ConfigurationProperties(prefix = "person")
public class Person {
```

#### 4、@ImportResource
SpringBoot推荐给容器添加组件的方式：
- 1、配置类=====Spring的xml配置文件（old）
- 2、全注解方式@Configuration+@Bean（new）

这两个方法均是Spring的配置Bean方法，前者为Spring的XML配置，后者为Spring的注解配置。

1、XML配置
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="HelloService" class="com.wdjr.springboot.service.HelloService"></bean>
</beans>
```
```java
@ImportResource(locations={"classpath:beans.xml"})
@SpringBootApplication
public class SpringBoot02ConfigApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBoot02ConfigApplication.class, args);
    }
}
```

2、注解配置
```java
/**
 * @Configuration：指明当前类是一个配置类；就是来代替之前的Spring配置文件
 *
 * 在配置文件中用<bean></bean>标签添加组件
 */

@Configuration
public class MyAppConfig {

    //将方法的返回值添加到容器中；容器这个组件id就是方法名
    @Bean
    public HelloService helloService01(){
        System.out.println("配置类给容器添加了HelloService组件");
        return new HelloService();
    }
}

 
```
```java
@Autowired
ApplicationContext ioc;

@Test
public void testHelloService(){
    boolean b = ioc.containsBean("helloService01");
    System.out.println(b);
}
```
### 2.2.4 配置文件占位符
#### 1、随机数
```java
${random.value} 、${random.int}、${random.long}
${random.int(10)}、${random.int[100,200]}
```
#### 2、获取配置值
```properties
person.age=${random.int}
person.boss=false
person.last-name=张三
person.maps.k1=v1
person.maps.k2=v2
person.lists=a,b,c
person.dog.name=${person.last-name}'s dog
person.dog.age=15
```
存在以下两种情况

如已声明的person.last-name则以申明值传入，即输出```张三's dog```

假设没有声明person.last-name会默认该参数为string，即输出```person.last-name's dog```

我们可以给在本行给未声明的参数赋值：
```properties
person.age=${random.int}
person.boss=false
person.last-name=张三${random.uuid}
person.maps.k1=v1
person.maps.k2=v2
person.lists=a,b,c
person.dog.name=${person.hello:hello}'s dog
person.dog.age=15
```
结果：输出```hello's dog```

### 2.2.5 Profile
#### 1、多Profile文件
我们在主配置文件编写的时候，文件名可以是```application-{profile}.properties/yml```

假设我们新建了两个propertis，分别用来开发配置和生产配置：
- application.properties
- application-dev.properties
- application-prod.properties

如果没有说明默认使用application.properties

如果我们需要使用其他的配置文件，我们需要在application.properties配置文件指定（因为默认先进application.properties）
```properties
spring.profiles.active=dev
```

#### 2、YAML文档块
在YAML中，我们使用---来划分文档块，通过profiles:来命名每个文档块，这样我们可以在一个YAML里完成多种配置文件的配置与替换
```yml
server:
  port: 8081
spring:
  profiles:
    active: dev

---

server:
  port: 9000
spring:
  profiles: dev

---
server:
  port: 80
spring:
  profiles: prod
```
#### 3、激活指定profile
1、**在配置文件中激活**

```properties
spring.profiles.active=dev
```

2、**命令行**

通过在cmd中以带参数形式启动jar来激活指定的配置文件
```
java -jar jar包名称 --spring.profiles.active=dev
```
该优先级大于配置文件

3、**虚拟机参数**

在IDEA中设置VM Option为```-Dspring.profiles.active=dev```

#### 4、加载配置文件位置
SpringBoot启动扫描以下位置的application.properties或者application.yml文件作为Spring boot的默认配置文件
- file:./config/(项目目录下的config文件夹中)
- file./(项目目录下)
- classpath:/config/(类目录下的config文件夹中，IDEA中resource文件夹内属于类目录)
- classpath:/(类目录下，我们默认创建Spring项目时默认放在此处)

优先级从高到低顺序，高优先级会覆盖低优先级的相同配置；

SpringBoot会把这四个文件全部加载（并不会丢弃低优先级文件），然后进行**互补配置**：加载完高优先级的所有配置后会加载次优先级配置中高优先级未配置的部分。

我们还可以通过spring.config.location 来设定最高优先级配置的默认路径

项目打包好了以后，可以使用命令行参数的形式，启动项目的时候来指定配置文件的新位置；指定配置文件和默认的配置文件会共同起作用，互补配置
```
java -jar spring-boot-config-02-0.0.1-SNAPSHOT.jar --spring.config.location=E:/work/application.properties
```
运维比较有用，从外部加载，不用修改别的文件。


#### 5.引入外部配置
SpringBoot也可以从以下位置加载配置；优先级从高到低；高优先级覆盖低优先级，可以互补

命令行参数
```
java -jar spring-boot-config-02-0.0.1-SNAPSHOT.jar --server.port=9005 --server.context-path=/abc
```
中间一个空格

优先加载profile, 由jar包外到jar包内

- jar包外部的application-{profile}.properties或application.yml(带Spring.profile)配置文件
- jar包内部的application-{profile}.properties或application.yml(带Spring.profile)配置文件
- jar包外部的application.properties或application.yml(带Spring.profile)配置文件
- jar包内部的application.properties或application.yml(不带spring.profile)配置文件

@Configuration注解类的@PropertySource

通过SpringApplication.setDefaultProperties指定的默认属性

#### 6、自动配置原理
配置文件到底能写什么？怎么写？自动配置原理；

[配置文件能配置的属性参照](https://docs.spring.io/spring-boot/docs/2.0.1.RELEASE/reference/htmlsingle/#common-application-properties)

**自动配置原理：**

1. SpringBoot启动的时候加载主配置类，开启了自动配置功能@EnableAutoConfiguration

2. @EnableAutoConfiguration的作用：
 - 利用EnableAutoConfigurationImportSelector给容器中导入一些组件
 - 将类路径下 MATE-INF/spring.factories里面配置的所有的EnableAutoConfiguration的值加入到了容器中；

3. 每一个自动配置类进行自动配置功能；
 - @Configuration，指定为配置类
 - @EnableConfigurationProperties（对应properties类名.class）：启动properties类的配置，将配置文件值与该properties类绑定
 - 对于每个单独的配置类来说，会根据当前不同的条件判断，决定这个配置类是否生效。
 - 一旦配置类生效；这个配置类就会给容器中添加各种组件（Bean），这些组件的属性是从对应的properties类中获取的，properties类的属性均来自配置文件。

xxxAutoConfiguration:自动配置类：给容器中添加组件

xxxProperties:封装配置文件中的属性；

跟之前的Person类一样，配置文件中值加入bean中。

**自动配置精髓**
1. SpringBoot启动会加载大量的自动配置类
2. 我们看我们需要的功能有没有SpringBoot默认写好的默认配置类；
3. 如果有在看这个自动配置类中配置了哪些组件；（只要我们要用的组件有，我们需要再来配置）
4. 给容器中自动配置添加组件的时候，会从properties类中获取属性。我们就可以在配置文件中指定这些属性的值

**自动配置报告**
因为自动配置类必须在一定条件下才会生效，所以有时候我们需要知道那些配置生效了

我们可以通过在配置文件中启用debug=true属性，配置文件，打印自动配合报告，这样就可以知道自动配置类生效

# 三、日志
## 3.1 日志框架
市面上的日志框架
| 日志门面（日志的抽象层） | 日志实现 |
| --- | --- |
| JCL、SLF4j、jboss-logging | Log4j、JUL、Log4j2 Logback |

左边选一个门面（抽象层）、右边来选一个实现；
- 日志门面：SLF4j；
- 日志实现：Logback；

SpringBoot：底层是Spring框架，Spring框架默认使用JCL，SpringBoot使用的是SLF4j和LogBack

## 3.2 SLF4j使用
### 3.2.1 如何在系统中使用SLF4j
开发过程中的日志记录方法的调用，不应该来直接调用日志的实现类，而是调用日志抽象层里面的方法；

系统中自带的SLF4j和Logback
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorld {
  public static void main(String[] args) {
    Logger logger = LoggerFactory.getLogger(HelloWorld.class);
    logger.info("Hello World");
  }
}
```

每个日志的实现框架都有自己的配置文件，使用slf4j后，配置文件还是做成日志实现框架自己本身的配置文件；

### 3.2.2 遗留问题

如果我们项目以前采用了其他日志框架，那我们**如何让系统中所有的日志都统一到SLF4j**；

1. 将系统中其他日志框架排除出去
2. 用中间包来替换原有的日志框架
3. 导入slf4j的对应实现



## 3.3 SpringBoot日志关系 

**SpringBoot的基础框架**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

**SpringBoot的日志功能**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-logging</artifactId>
    <version>2.0.1.RELEASE</version>
    <scope>compile</scope>
</dependency>
```

- SpringBoot底层也是使用slf4j+logback的方式进行日志记录
- SpringBoot将其他的日志都通过中间替换包换成了slf4j（中间包的包名和其他日志包名一致，但是使用的slf4j的内容）
- 如果我们要引入其他框架，必须把这个框架的默认日志依赖移除掉（因为默认日志和中间包包名一致会产生冲突）

## 3.4 日志使用

### 3.4.1 默认配置

SpringBoot默认帮我们进行了默认的日志配置。

```java
Logger logger = LoggerFactory.getLogger(getClass());

@Test
public void contextLoads() {
    //日志级别由低到高
    logger.trace("这是trace日志...");
    logger.debug("这是debug日志...");
    //SpringBoot默认使用info级别，trace和debug不会输出
    logger.info("这是info日志...");
    logger.warn("这是warn日志...");
    logger.error("这是error日志...");
}
```

在application.properties中更改log的默认配置

```properties
#logging.path=
#不指定路径的情况下会在当前项目下生成指定名称的log日志
#也可以在file中指定绝对路径如G:/spring.log的方式创建log日志
#logging.file=springboot.log

#如果未指定文件的情况下，会在当前磁盘的根目录下创建log日志，名称默认为spring.log
logging.path=/spring/log

#在控制台输出的日志格式
logging.pattern.console=
#指定文件中日志输出的格式
logging.pattern.file=
```

日志输出格式

```properties
#控制台输出的日志格式 
#%d：日期
#%thread：线程号 
#%-5level：靠左 级别 
#%logger{50}：全类名50字符限制,否则按照句号分割
#%msg：消息+换行
#%n：换行
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n
```

| logging.file | logging.path | Example  | Description                                |
| :----------- | :----------- | :------- | :----------------------------------------- |
| (none)       | none         | none     | 只在控制台输出                             |
| 指定文件名   | none         | my.log   | 输出到my.log文件                           |
| (none)       | 指定目录     | /var/log | 输出到当前磁盘下指定目录的spring.log文件中 |

### 3.4.2 指定配置

给类路径下放上每个日志框架自己的配置框架；SpringBoot就不会使用自己默认的配置

| logging System         | Customization                                                |
| ---------------------- | ------------------------------------------------------------ |
| Logback                | logback-spring.xml ,logback-spring.groovy,logback.xml or logback.groovy |
| Log4J2                 | log4j2-spring.xml or log4j2.xml                              |
| JDK(Java Util Logging) | logging.properties                                           |

这里Spring官方推荐使用带有spring后缀的命名格式：

- logback.xml：直接呗日志框架识别调用
- logback-spring.xml：日志框架不会直接加载配置项，而是由SpringBoot解析日志配置，这样可以使用SpringBoot的高级Profile功能。

```xml
<springProfile name="dev">
	<!-- 可以指定某段配置只在某个环境下生效 -->
</springProfile>
<springProfile name!="dev">
	<!-- 可以指定某段配置只在某个环境下生效 -->
</springProfile>
```



# 四、Web开发

## 4.1 简介

**使用SpringBoot**：

1. 创建SpringBoot应用，选中所需的模块；
2. SpringBoot通过自动配置完成场景配置，只需要在配置文件中指定少量配置就可以运行起来
3. 自己编写业务代码；

**自动配置原理？**

这个场景SpringBoot帮我们配置了什么？能不能修改？能修改哪些配置？能不能扩展？

```
xxxxAutoConfiguration：帮我们给容器中自动配置组件；
xxxxProperties：配置类来封装
```



## 4.2 SpringBoot对静态资源的映射规则

```java
@ConfigurationProperties(prefix = "spring.resources", ignoreUnknownFields = false)
public class ResourceProperties implements ResourceLoaderAware, InitializingBean {
    //可以设置和静态资源相关的参数，缓存时间等
```

```java
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    if (!this.resourceProperties.isAddMappings()) {
        logger.debug("Default resource handling disabled");
    } 
    else {
        Duration cachePeriod = this.resourceProperties.getCache().getPeriod();
        CacheControl cacheControl = 
            this.resourceProperties.getCache().getCachecontrol().toHttpCacheControl();
        if (!registry.hasMappingForPattern("/webjars/**")) {
            this.customizeResourceHandlerRegistration(
                registry.addResourceHandler(new String[]{"/webjars/**"})
                .addResourceLocations(new String[]{"classpath:/META-INF/resources/webjars/"})
                .setCachePeriod(this.getSeconds(cachePeriod)).setCacheControl(cacheControl));
        }

        String staticPathPattern = this.mvcProperties.getStaticPathPattern();
        if (!registry.hasMappingForPattern(staticPathPattern)) {
            this.customizeResourceHandlerRegistration(registry.addResourceHandler(new String[]{staticPathPattern}).addResourceLocations(getResourceLocations(this.resourceProperties.getStaticLocations())).setCachePeriod(this.getSeconds(cachePeriod)).setCacheControl(cacheControl));
        }

    }
}
```

1. 所有/webjars/**，都去 classpath:/META-INF/resources/webjars/ 找资源；

   webjars：以jar包的方式引入静态资源；

   localhost:8080/webjars/jquery/3.4.1/jquery.js



2. "/**"访问当前项目的任何资源

   会在这几文件夹下去找静态路径（静态资源文件夹）

   ```
   "classpath:/META-INF/resources/", 
   "classpath:/resources/",
   "classpath:/static/", 
   "classpath:/public/",
   "/";当前项目的根路径
   ```

   localhost:8080/abc 等同于去类路径下找静态资源abc

   

3. 欢迎页；静态资源文件夹下的所有index.html页面

   localhost:8080/ 等同找index页面

   

4. 所有的**/favicon.ico 都是在静态资源文件下找

   favicon.ico 用于更改网页图标



## 4.3 模板引擎

JSP、Velocity、Freemarker、Thymeleaf

SpringBoot推荐使用Thymeleaf；

### 4.3.1 引入thymeleaf

```xml
<dependency>
    <groupId>org.thymeleaf</groupId>
    <artifactId>thymeleaf-spring5</artifactId>
</dependency>
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-java8time</artifactId>
</dependency>
```

### 4.3.2 Thymeleaf使用和语法

```java
public class ThymeleafProperties {
    private static final Charset DEFAULT_ENCODING;
    
    //只要我们把HTML页面放在classpath:/templates/下即可使用
    public static final String DEFAULT_PREFIX = "classpath:/templates/";
    public static final String DEFAULT_SUFFIX = ".html";  
   
```

**使用**

1. 导入thymeleaf的名称空间

   ```xml
   <html lang="en" xmlns:th="http://www.thymeleaf.org">
   ```

2. 使用thymeleaf语法

   ```html
   <!DOCTYPE html>
   <html lang="en" xmlns:th="http://www.thymeleaf.org">
   <head>
       <meta charset="UTF-8">
       <title>Title</title>
   </head>
   <body>
       <h1>Succeed!!!</h1>
       <!--th:text 将div里面的文本内容设置为 -->
       <div th:text="${hello}">这是欢迎信息</div>
   </body>
   </html>
   ```

### 4.3.3 语法规则

1. th:text：改变当前元素里面的文本内容

   **在SpringBoot的环境下**

   ```html
   <div id="testid" class="testcalss" th:id="${Lion}" th:class="${Lion}" th:text="${Lion}">
   	前端数据
   </div>
   ```

|      | 功能                            | 标签                                 | 功能                                    |
| ---- | ------------------------------- | ------------------------------------ | --------------------------------------- |
| 1    | Fragment inclusion              | th:insert th:replace                 | include(片段包含)                       |
| 2    | Fragment iteration              | th:each                              | c:forEach(遍历)                         |
| 3    | Conditional evaluation          | th:if th:unless th:switch th:case    | c:if(条件判断)                          |
| 4    | Local variable definition       | th:object th:with                    | c:set(声明变量)                         |
| 5    | General attribute modification  | th:attr th:attrprepend th:attrappend | 属性修改支持前面和后面追加内容          |
| 6    | Specific attribute modification | th:value th:href th:src ...          | 修改任意属性值                          |
| 7    | Text (tag body modification)    | th:text th:utext                     | 修改标签体内容  utext：不转义字符大标题 |
| 8    | Fragment specification          | th:fragment                          | 声明片段                                |

## 4.4 SpringMVC自动配置

### 4.4.1 自动配置SpringMVC

SpringBoot自动配置好了SpringMVC，以下时SpringBoot对SpirngMVC的默认：

- 包含 `ContentNegotiatingViewResolver` 和 `BeanNameViewResolver` beans.
  - 自动配置类ViewerResolver（视图解析器：根据方法的返回值得到试图对象（view），视图对象决定如何渲染（转发、重定向））
  - ContentNegotiatingViewResolver：组合所有视图解析器
  - 定制解析器：我们可以自己给容器中添加一个视图解析器，自动将其组合进来
- 支持静态资源文件加路径，包含支持Webjars
- 自动注册了 `Converter`, `GenericConverter`, and `Formatter` beans.
  - Converter：转换器，类型转换
  - Formatter：格式化器；
- 支持 `HttpMessageConverters`
  - HttpMessageConverter：SpringMVC用来转换Http请求和响应的
  - HttpMessageConverters是从容器中确定；获取所有的HttpMessageConverter
- Automatic registration of `MessageCodesResolver` 
- Static `index.html` support（支持静态首页访问）.
- Custom `Favicon` support （支持自定义默认图标）
- Automatic use of a `ConfigurableWebBindingInitializer` bean 

### 4.4.2 扩展SpringMVC

编写一个配置类（@Configuration）

## 4.5 如何修改SpringBoot的默认配置

模式：

1. SpringBoot在自动配置很多组建的时候，先看容器中有没有用户自己配置的（@Bean、@Component）如果有就用用户配置的，如果没有才自动配置；如果有些组建可以有多个，就和默认的一起整合。
2. 