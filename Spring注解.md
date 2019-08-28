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

## 2.1 概述

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

### 2.2.4 BeanPostProcessor[interface]

bean 的后置处理器，在 bean 初始化前后进行一些工作

- **postProcessBeforeInitialization**：在初始化之前工作
- **postProcessAfterInitialization**：在初始化之后工作

```java
@Component
public class beanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println(bean + "===>" + beanName + "===> BeforeInit");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println(bean + "===>" + beanName + "===> AfterInit");
        return bean;
    }
}
```

我们设置在初始化前后分别输出一行语句

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
}
```

以Dog类来进行测试

```java
Dog Constuctor
com.TDVictory.bean.Dog@769e7ee8===>dog===> BeforeInit
dog PostConstruct
com.TDVictory.bean.Dog@769e7ee8===>dog===> AfterInit
```

我们可以看到首先调用了构造器方法，然后是初始化前——初始化——初始化后。

接下来我们查看一下源码

```java
//初始化前
if (mbd == null || !mbd.isSynthetic()) {
    wrappedBean = this.applyBeanPostProcessorsBeforeInitialization(bean, beanName);
}

//初始化
try {
    this.invokeInitMethods(beanName, wrappedBean, mbd);
} catch (Throwable var6) {
    throw new BeanCreationException(mbd != null ? mbd.getResourceDescription() : null, beanName, "Invocation of init method failed", var6);
}

//初始化后
if (mbd == null || !mbd.isSynthetic()) {
    wrappedBean = this.applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
}
```

遍历并执行容器中的BeanPostProcessors，一但为空则跳出循环

```java
public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
    Object result = existingBean;
    Object current;
    //遍历并执行容器中的BeanPostProcessors，一但为空则跳出循环
    for(Iterator var4 = this.getBeanPostProcessors().iterator(); var4.hasNext(); result = current) {
        BeanPostProcessor processor = (BeanPostProcessor)var4.next();
        current = processor.postProcessBeforeInitialization(result, beanName);
        if (current == null) {
            return result;
        }
    }

    return result;
}
```

# 三、属性赋值

## 3.1 @Value

使用@Value赋值：

1. 基本数值
2. SpEL：#{ }
3. 取出配置文件中的值：${ }

### 3.1.1 直接赋值

我们设置一个Person类：

```java
public class Person {
    @Value("张三")
    private String name;
    @Value(12)
    private int age;    
    ...
}
```

将其注入容器

```java
@Configuration
public class MainConfig4 {
    @Bean
    public Person person(){
        return new Person();
    }
}
```

获取其属性

```java
public class IOCTest4 {
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig4.class);

    @Test
    public void test01(){
        printBeans(applicationContext);
        System.out.println("======================================");
        Person person = applicationContext.getBean(Person.class);
        System.out.println(person.getName() + "===>" + person.getAge());
    }

    public void printBeans(ApplicationContext applicationContext){
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanName:beanDefinitionNames
             ) {
            System.out.println(beanName);
        }
    }
}
```

得到结果

```
mainConfig4
person
======================================
张三===>12
```

### 3.1.2 使用SpEL语句

我们可以在Value参数中使用SpEL语句来设置参数值

```java
@Value("#{22-10}")
private int age;
```

这样得到的结果和上面一致

```
mainConfig4
person
======================================
张三===>12
```

## 3.2 @PropertySource

### 导入配置文件参数

很多时候为了方便修改，我们的参数都写在配置文件里。我们可以通过导入配置文件参数来给对应属性赋值。

我们创建一个Properties，设置nickName为zhangsan

```properties
person.nickName = zhangsan
```

然后在配置类中设置配置文件

```java
@PropertySource(value = {"classpath:/Person.properties"})
@Configuration
public class MainConfig4 {
    @Bean
    public Person person(){
        return new Person();
    }
}
```

这样我们就可以在Person类中使用配置文件参数了

```java
@Value("${person.nickName}")
private String nickName;
```

```java
@Test
public void test01(){
    printBeans(applicationContext);
    System.out.println("======================================");
    Person person = applicationContext.getBean(Person.class);
    System.out.println(person.getName() + "===>" + person.getAge());
    System.out.println(person.getNickName());
}
```

输出结果

```
mainConfig4
person
======================================
张三===>12
zhangsan
```

# 四、自动装配

Spring 利用依赖注入（DI），完成对 IOC 容器中各个组件的依赖关系赋值；

## 4.1 @Autowired

@Autowired 将会自动注入属性。

### 4.1.1 示例

我们创建BookService，BookDao，将其注册到容器中进行管理。再将BookDao自动绑定到BookService。

```java
@Service
public class BookService {
    @Autowired
    private BookDao bookDao;

    public void print(){
        System.out.println(bookDao);
    }
}
```

```java
@Repository
public class BookDao {
}
```

接下来我们输出一下BookService中的bookDao

```java
@Test
public void test01(){
    System.out.println("======================================");
    BookService bookService = applicationContext.getBean(BookService.class);
    bookService.print();
    BookDao bookDao = applicationContext.getBean(BookDao.class);
    System.out.println(bookDao);
}
```

输出结果如下：

```
com.TDVictory.dao.BookDao@16e7dcfd
com.TDVictory.dao.BookDao@16e7dcfd
```

我们可以看到，会自动按照类型去容器中找对应的组件并自动装配。

### 4.1.2 装配优先级

- 默认优先按照类型去容器中寻找对应的组件

- 如果找到多个相同的组件

  - 将属性的参数名作为id再次进行查找筛选

  ```java
  //优先绑定id为bookDao的Bean    
  @Autowired
  private BookDao bookDao;
  ```

  - 如果对应Bean标注有@Primary，则优先使用该Bean

  ```java
  //优先使用id为bookDao2的Bean
  @Primary
  @Bean("bookDao2")
  public BookDao bookDao(){
      return new BookDao();
  } 
  ```

  - 如果标注有@Qualifier，则会以@Qualifier指定的id进行装配而非使用组件名或者优先级Bean

  ```java
  //指定使用id为bookDao2的Bean
  @Qualifier("bookDao2")
  @Autowired
  private BookDao bookDao;
  ```

- 如果没有找到组件

  - 默认情况下会报错
  - 通过设置@Autowired(require = false)来设置成没找到组件则返回null

  ```java
  @Autowired(required = false)
  private BookDao bookDao;
  ```

### 4.1.3 其他注入方式

标注在参数位置上的@Autowired比较直观，除此之外我们也可以标注在方法和构造器上来实现自动装配。

通过@Bean传入的参数均来自于容器。等效于已标注@Autowired。

## 4.2 @Resource & @Inject

@Resource 和 @Inject 是Java自带的注解。功能较Autowired差，但是由于是Java自带，可以在非Spring环境下使用。

- @Resource：可以和@Autowired一样实现自动装配，但默认按照组件名称进行装配

```java
//默认情况装配参数名id的bean
@Resource
private BookDao bookDao;

//设置后装配设置名的id
@Resource(name = "bookDao2")
private BookDao bookDao;
```

- @Inject：需要导入javax.inject 的包，效果和Autowired一样。没有 require = false的功能

## 4.3 Aware

自定义组件想要使用Spring容器底层的一些组件（ApplicationContext，BeanFactory等（xxx））。我们通过继承xxxAware。

Aware 接口是为了能够感知到自身的一些属性。  比如实现了BeanNameAware接口的类，能够获取到自身的Name属性，实现了 ApplicationContextAware 接口的类，能够获取到ApplicationContext，实现了BeanFactoryAware接口的类，能够获取到BeanFactory对象。

## 4.4 Profile

Profile 是 Spring 为我们提供的可以根据当前环境，动态激活和切换一系列组件的功能。

#### 注册组件/配置类

@Profile：指定组件在哪个环境下才能被注册到容器中，不指定的情况下则任何环境都能注册该组件。

```java
@Profile("test")
@Bean
public Person person(){
    return new Person();
}
```

我们也可以在配置类上指定整个配置类环境。只有在指定环境下配置类才会生效

```java
@Profile("test")
@Configuration
public class MainConfig6 {
    
    @Bean
    public Person person(){
        return new Person();
    }
}
```

####切换环境

1. 我们可以通过在命令行输入指令切换环境：在虚拟机参数位置加载 `-Dspring.profiles.active=test`，此时环境为test环境
2. 我们可以通过代码，更改上下文来激活环境

```java
//将上下文通过无参进行构造（ApplicationContext没有激活环境的功能）
AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
//激活环境
applicationContext.getEnvironment().setActiveProfiles("test");
//设置配置类
applicationContext.register(MainConfig6.class);
//刷新
applicationContext.refresh();
```

