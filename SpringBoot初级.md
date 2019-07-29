# 一、Spring Boot入门
## 1、Spring Boot 简介
- 简化Spring应用开发的一个框架；
- 整个Spring技术栈的一个大整合；
- J2EE开发的一站式解决方案；

## 2、微服务
微服务：架构风格（服务微化）

一个应用应该是一组小型服务；可以通过HTTP的方式来进行互通

每一个功能元素最终都是一个可独立替换和独立升级的软件单元；

## 3、基础探究
### 1.POM文件
#### 1.父项目
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

#### 2.启动器
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

### 2.主程序类，主入口类
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

### 3、使用Spring Initializer创建一个快速向导
1.IDE支持使用Spring Initializer

自己选择需要的组件:例如web

默认生成的SpringBoot项目

主程序已经生成好了，我们只需要完成我们的逻辑

resources文件夹目录结构
- static:保存所有的静态文件；js css images
- templates:保存所有的模板页面；（Spring Boot默认jar包使用嵌入式的Tomcat,默认不支持JSP）；可以使用模板引擎（freemarker.thymeleaf）;
- application.properties:Spring Boot的默认配置，例如 server.port=9000

# 二、配置文件
## 1.配置文件
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

## 2.YAML语法：
### 1.基本语法
k:（空格）v:表示一对键值对（空格必须有）

以空格的缩进来控制层级关系；只要是左对齐的一列数据，都是同一个层级
```yaml
server:
    port:8081
    path:/hello
```

### 2.值的写法
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

### 3.配置文件值注入
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

#### 3.@PropertySource注解
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

#### 4.@ImportResource
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
