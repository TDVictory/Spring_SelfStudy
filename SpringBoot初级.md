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

