# Spring 注解驱动开发

# 一、容器注册

给容器中注册组件

## 1.1 组件标注 + 包扫描

该方法主要使用于我们自己编写的脚本

## 1.2 @Bean

该方法主要用于导入第三方包内的组件

## 1.3 @Import

快速给容器中导入一个组件

### 1.3.1 @Import()

### 1.3.2 ImportSelector

### 1.3.3 ImportBeanDefinitionRegistrar

## 1.4 FactoryBean

工厂Bean



# 二、生命周期

bean 的生命周期指的是：bean 的创建-->初始化-->销毁。

容器管理 bean 的生命周期

我们可以自定义初始化和销毁方法：容器在 Bean 进行到当前生命周期的时候调用我们自定义的初始化和销毁方法

#### 构造（对象创建）

- 单实例：在容器启动的时候创建对象（也可以通过@Lazy来设置成获取时创建对象）
- 多实例：在每次获取的时候创建对象

#### 初始化

对象创建完成，并赋值完成后调用初始化方法

#### 销毁

- 单实例：容器关闭的时候会调用销毁方法
- 多实例：容器不会管理这个 bean，容器关闭的时候不会调用销毁方法。可以通过手动调用销毁方法

## 2.2 指定初始化和销毁方法

### 2.2.1 initMethod & destroyMethod

**@Bean** 中可以设置参数 **initMethod** 和 **destroyMethod** 来设置初始化和销毁方法

```java
//创建示例类Car
public class Car {
    public Car() {
        System.out.println("Car Constuct");
    } 

    public void init(){
        System.out.println("Car Init");
    }

    public void destroy(){
        System.out.println("Car Destroy");
    }
}
```

```java
@Configuration
public class MainConfig3 {
    //设置bean的初始化方法和销毁方法
    @Bean(initMethod = "init",destroyMethod = "destroy")
    public Car car(){
        return new Car();
    }
}
```

```java
@Test
public void testCar(){
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig3.class);
    System.out.println("容器创建完成");
    Car car = applicationContext.getBean(Car.class);
    ((AnnotationConfigApplicationContext) applicationContext).close();
}
```

最后得到的输出结果为

```
Car Constuct
Car Init
容器创建完成
Car Destroy
```

#### 多实例

如果通过@Scope 设置其为多实例

```java
@Configuration
public class MainConfig3 {

    @Bean(initMethod = "init",destroyMethod = "destroy")
    @Scope(value = "prototype")
    public Car car(){
        return new Car();
    }
}
```

则输出结果为

```
容器创建完成
Car Constuct
Car Init
```

### 2.2.2 InitializingBean & DisposableBean

**InitializingBean** 和 **DisposableBean** 分别为控制初始化和销毁的接口。分别实现接口方法**afterPropertiesSet()** 和 **destroy()** 来实现初始化和销毁的控制。

```java
public class Cat implements InitializingBean, DisposableBean {
    public Cat() {
        System.out.println("Cat Constuctor");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("Cat Destroy");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Cat afterPropertiesSet");
    }
}
```

```java
@Bean
public Cat cat(){
    return new Cat();
}
```

```java
@Test
public void testCat(){
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig3.class);
    System.out.println("容器创建完成");
    Cat cat = applicationContext.getBean(Cat.class);
    ((AnnotationConfigApplicationContext) applicationContext).close();
}
```

输出结果为

```
Cat Constuctor
Cat afterPropertiesSet
容器创建完成
Cat Destroy
```

### 2.2.3 @PostConstuct & @PreDestroy

该注解为Java自带注解

- **@PostConstuct**：标注 @PostConstuct 的方法在 bean 创建完成并且属性赋值完成后执行
- **@PreDestroy**：标注 @PreDestroy 的方法在容器销毁 bean 之前执行

```java
@Component
public class Dog {
    public Dog() {
        System.out.println("Dog Constuctor");
    }
    
    @PostConstruct
    public void init(){
        System.out.println("dog PostConstruct");
    }
    
    @PreDestroy
    public void destroy(){
        System.out.println("dog PreDestroy");
    }
}
```

同样我们通过@Component将其加入到容器中

```java
@Test
public void testDog(){
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig3.class);
    System.out.println("容器创建完成");
    Dog dog = applicationContext.getBean(Dog.class);
    ((AnnotationConfigApplicationContext) applicationContext).close();
}
```

输出结果为

```
Dog Constuctor
dog PostConstruct
容器创建完成
dog PreDestroy
```

### 2.2.4 