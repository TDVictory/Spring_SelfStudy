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

# 五、AOP

AOP：指在程序运行期间，动态地将某段代码切入到指定方法指定位置进行运行的编程方式。

通知方法有：

- 前置通知（@Before）：在目标运行之前运行
- 后置通知（@After）：在目标运行结束之后运行
- 返回通知（@AfterReturning）：在目标正常返回之后运行
- 异常通知（@AfterThrowing）：在目标方法出现异常以后运行
- 环绕通知（@Around）：动态代理，手动推进目标方法运行

## 5.1 简单示例

#### 导入依赖

Spring 对 AOP进行了一定的封装，我们导入spring-aspects 模块添加依赖。

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aspects</artifactId>
    <version>5.1.5.RELEASE</version>
</dependency>
```

#### 定义业务逻辑类

我们希望在业务运行的时候会有日志输出，但是我们不希望日志输出和主体业务耦合。

```java
public class MathCalculator {
    public int div(int i,int j){
        //System.out.println("log"); 如果日志输出放在业务逻辑主体内，就会产生耦合
        return i/j;
    }
}
```

#### 定义日志切面类

我们希望通过日志切面类来感知业务逻辑类的运行状况

```java
//标注为切面类，只有标注成切面类才能被Spring识别
@Aspect
public class LogAspects {

    @Before("execution(public int com.TDVictory.aop.MathCalculator.div(int,int))")
    public void logStart(){
        System.out.println("Start");
    }

    @After("execution(public int com.TDVictory.aop.MathCalculator.div(int,int))")
    public void logEnd(){
        System.out.println("End");
    }

    @AfterReturning("execution(public int com.TDVictory.aop.MathCalculator.div(int,int))")
    public void logReturn(){
        System.out.println("Return{}");
    }

    @AfterThrowing("execution(public int com.TDVictory.aop.MathCalculator.div(int,int))")
    public void logException(){
        System.out.println("Exception");
    }
}

```

注解中的execution用来表示这个切面类中的该方法在哪里执行，也就是作用的目标。

为了避免我们重复写`"execution(public int com.TDVictory.aop.MathCalculator.div(int,int))"`我们可以设定一个锚点`@Pointcut`。

```java
@Aspect
public class LogAspects {

    //我们设置锚点@Pointcut，通过*来表示在com.TDVictory.aop.MathCalculator下具有任意参数和返回值的任意方法
    @Pointcut("execution(* com.TDVictory.aop.MathCalculator.*(..))")
    public void pointCut(){
    }

    //在同类调用时可以不写全包名
    @Before("pointCut()")
    public void logStart(){
        System.out.println("Start");
    }

    @After("pointCut()")
    public void logEnd(){
        System.out.println("End");
    }

    @AfterReturning("pointCut()")
    public void logReturn(){
        System.out.println("Return{}");
    }

    @AfterThrowing("pointCut()")
    public void logException(){
        System.out.println("Exception");
    }
}
```

作为日志工具，我们希望获取当前业务逻辑类的具体参数进行分析，而不仅仅是输出一个固定的字符串。根据不同的通知方法，我们就可以得到业务逻辑各类的参数

#### 加入容器

我们设置好配置类，将业务逻辑类和日志切面类加入容器。同时配置类通过`@EnableAspectJAutoProxy`来启用切面功能。

```java
//启用切面功能
@EnableAspectJAutoProxy
@Configuration
public class MainConfigOfAOP {
    @Bean
    public MathCalculator mathCalculator(){
        return new MathCalculator();
    }

    @Bean
    public LogAspects logAspects(){
        return new LogAspects();
    }
}
```

#### 运行测试

```java
public class AOPTest {
    AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(MainConfigOfAOP.class);
    @Test
    public void aopTest(){
        MathCalculator mathCalculator = annotationConfigApplicationContext.getBean(MathCalculator.class);
        System.out.println(mathCalculator.div(6,2));
    }
}
```

#### 输出结果

```java
Start
End
Return{}
3
```

## 5.2 AOP 原理解析

### 5.2.1 开启AOP功能 

> @EnableAspectJAutoProxy

在上述案例中我们可以看到，我们在配置类上添加了`@EnableAspectJAutoProxy`注解，这样我们开启了切面功能，我们看一下`@EnableAspectJAutoProxy`的具体原理。

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({AspectJAutoProxyRegistrar.class})
public @interface EnableAspectJAutoProxy {
    boolean proxyTargetClass() default false;

    boolean exposeProxy() default false;
}

```

可以看到，这个注解通过`@import`将`AspectJAutoProxyRegistrar`这个组件添加到容器中。

而`AspectJAutoProxyRegistrar`这个组件将会给容器中注册一个`AnnotationAwareAspectJAutoProxyCreator`

**AnnotationAwareAspectJAutoProxyCreator**是AOP功能的后置处理器。

### 5.2.2 容器创建

> AnnotationAwareAspectJAutoProxyCreator

在我们创建上下文时

```java
AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(MainConfigOfAOP.class);
```

#### 创建后置处理器实例

其中会**注册所有的后置处理器**，为他们创建实例

```java
// Register bean processors that intercept bean creation.
registerBeanPostProcessors(beanFactory);
```

#### 创建业务逻辑类和切面组件

后置处理器创建完成后**创建业务逻辑类和切面组件**

```java
// Instantiate all remaining (non-lazy-init) singletons.
finishBeanFactoryInitialization(beanFactory);
```

后置处理器会拦截组件创建过程

```java
//因为是顺序执行，会优先执行resolveBeforeInstantiation方法，这个方法将会优先用后置处理器来创建bean
try {
    // Give BeanPostProcessors a chance to return a proxy instead of the target bean instance.
    Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
    if (bean != null) {
        return bean;
    }
}
catch (Throwable ex) {
    throw new BeanCreationException(mbdToUse.getResourceDescription(), beanName,"BeanPostProcessor before instantiation of bean failed", ex);
}

//如果后置处理器无法创建bean，则正常调用doCreateBean创建bean。
try {
    Object beanInstance = doCreateBean(beanName, mbdToUse, args);
    if (logger.isTraceEnabled()) {
        logger.trace("Finished creating instance of bean '" + beanName + "'");
    }
    return beanInstance;
}
```

#### 增强组件

组件创建完成之后，判断组件是否需要增强

```java
@Override
public Object postProcessAfterInitialization(@Nullable Object bean, String beanName) {
    if (bean != null) {
        Object cacheKey = getCacheKey(bean.getClass(), beanName);
        if (this.earlyProxyReferences.remove(cacheKey) != bean) {
            //如果需要增强组件则进行再包装
            return wrapIfNecessary(bean, beanName, cacheKey);
        }
    }
    return bean;
}
```

将切面的通知方法包装成增强器（Advisor）；非业务逻辑组件创建一个代理对象

```java
protected Object[] getAdvicesAndAdvisorsForBean(
    Class<?> beanClass, String beanName, @Nullable TargetSource targetSource) {
    //MathCalculator会在这里获得5个advisor，一个默认四个设定
    List<Advisor> advisors = findEligibleAdvisors(beanClass, beanName);
    if (advisors.isEmpty()) {
        return DO_NOT_PROXY;
    }
    return advisors.toArray();
}
```

```java
// 如果存在增强器，则创建代理
Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null);
if (specificInterceptors != DO_NOT_PROXY) {
    this.advisedBeans.put(cacheKey, Boolean.TRUE);
    Object proxy = createProxy(
        bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));
    this.proxyTypes.put(cacheKey, proxy.getClass());
    return proxy;
}
//反之则正常返回bean
this.advisedBeans.put(cacheKey, Boolean.FALSE);
return bean;
```



### 5.2.3 执行目标方法

1. 代理对象执行目标方法

2. 通过获取目标的拦截器链。

   ```java
   public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
       Object oldProxy = null;
       boolean setProxyContext = false;
       Object target = null;
       TargetSource targetSource = this.advised.getTargetSource();
       try {
           if (this.advised.exposeProxy) {
               oldProxy = AopContext.setCurrentProxy(proxy);
               setProxyContext = true;
           }
           target = targetSource.getTarget();
           Class<?> targetClass = (target != null ? target.getClass() : null);
           //这里获取了目标的拦截器链chain
           List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
   ...
       }
   }
   ```

3. 利用其链式机制，以此进入每一个拦截器进行执行

   ```java
   // Check whether we only have one InvokerInterceptor: that is,
   // no real advice, but just reflective invocation of the target.
   if (chain.isEmpty() && Modifier.isPublic(method.getModifiers())) {
       // We can skip creating a MethodInvocation: just invoke the target directly.
       // Note that the final invoker must be an InvokerInterceptor, so we know
       // it does nothing but a reflective operation on the target, and no hot
       // swapping or fancy proxying.
       Object[] argsToUse = AopProxyUtils.adaptArgumentsIfNecessary(method, args);
       retVal = methodProxy.invoke(target, argsToUse);
   }
   else {
       //如果链不为空，我们将进入拦截器链依次执行
       retVal = new CglibMethodInvocation(proxy, target, method, args, targetClass, chain, methodProxy).proceed();
   }
   ```

   ```java
   public Object proceed() throws Throwable {
       //currentInterceptorIndex初始值默认为-1，interceptorsAndDynamicMethodMatchers为拦截器链上的所有拦截器（上文中分别为1默认4设定，共5个）
       if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
           //如果读取完所有的拦截器则返回
           return invokeJoinpoint();
       }
       //每次获取currentInterceptorIndex+1上的拦截器，也就是从0开始
       Object interceptorOrInterceptionAdvice =
           this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
       //中间这部分不是很清楚= =，案例中不会执行到
       if (interceptorOrInterceptionAdvice instanceof InterceptorAndDynamicMethodMatcher) {
           // Evaluate dynamic method matcher here: static part will already have
           // been evaluated and found to match.
           InterceptorAndDynamicMethodMatcher dm =
               (InterceptorAndDynamicMethodMatcher) interceptorOrInterceptionAdvice;
           Class<?> targetClass = (this.targetClass != null ? this.targetClass : this.method.getDeclaringClass());
           if (dm.methodMatcher.matches(this.method, targetClass, this.arguments)) {
               return dm.interceptor.invoke(this);
           }
           else {
               // Dynamic matching failed.
               // Skip this interceptor and invoke the next in the chain.
               return proceed();
           }
       }
       //我们获取的拦截器的顺序是默认拦截器，Throwing，Returning，After，Before，会依次执行invoke。因为实际上是个递归调用，所以实际上的执行顺序是相反的
       else {
           // It's an interceptor, so we just invoke it: The pointcut will have
           // been evaluated statically before this object was constructed.
           return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);
       }
   }
   ```

   ```java
   //默认的invoke方法，主要做一些注册操作
   @Override
   public Object invoke(MethodInvocation mi) throws Throwable {
       MethodInvocation oldInvocation = invocation.get();
       invocation.set(mi);
       try {     
           //这里执行proceed又会回到上面代码，此时的currentInterceptorIndex已经加1，就会调用下一个拦截器Throwing的invoke
           return mi.proceed();
       }
       finally {
           invocation.set(oldInvocation);
       }
   }
   ```

   ```java
   public class AspectJAfterThrowingAdvice extends AbstractAspectJAdvice
   		implements MethodInterceptor, AfterAdvice, Serializable {
   	...
   	@Override
   	public Object invoke(MethodInvocation mi) throws Throwable {
   		try {
   		//继续递归,下一个是Returning
   			return mi.proceed();
   		}
   		catch (Throwable ex) {
   			if (shouldInvokeOnThrowing(ex)) {
   			//如果发生Exception，则会执行对应方法
   				invokeAdviceMethod(getJoinPointMatch(), null, ex);
   			}
   			throw ex;
   		}
   	}
   	...
   }
   ```

   ```java
   public class AfterReturningAdviceInterceptor implements MethodInterceptor, AfterAdvice, Serializable {
   	...
   	@Override
   	public Object invoke(MethodInvocation mi) throws Throwable {
           ////进入下一个拦截器After
   		Object retVal = mi.proceed();
           //如果传上来的retVal是异常，就不会执行Returning的方法
   		this.advice.afterReturning(retVal, mi.getMethod(), mi.getArguments(), mi.getThis());
   		return retVal;
   	}
   }
   ```

   ```java
   public class AspectJAfterAdvice extends AbstractAspectJAdvice
   		implements MethodInterceptor, AfterAdvice, Serializable {
   	...
   	@Override
   	public Object invoke(MethodInvocation mi) throws Throwable {
   		try {
   		//进入下一个拦截器Before
   			return mi.proceed();
   		}
   		finally {
   		//无论运行过程中是否抛出异常，都会执行After的方法
   			invokeAdviceMethod(getJoinPointMatch(), null, null);
   		}
   	}
   	...
   }
   ```

   ```java
   public class MethodBeforeAdviceInterceptor implements MethodInterceptor, BeforeAdvice, Serializable {
   ...
   	@Override
   	public Object invoke(MethodInvocation mi) throws Throwable {
       //首先执行指定的Before方法
   		this.advice.before(mi.getMethod(), mi.getArguments(), mi.getThis());
       //因为这是最后一个拦截器，再往下运行就会执行原本的运算功能
   		return mi.proceed();
   	}
   }
   ```

   

4. 顺序如下：

   1. 执行前置通知
   2. 执行目标方法
   3. 执行后置通知
   4. 执行返回通知
   5. 如果后置出现异常则执行返回通知

# 六、声明式事务

1. 导入相关依赖（数据源、数据库驱动、Spring-jdbc模块）

   ```xml
   <!-- https://mvnrepository.com/artifact/com.mchange/c3p0 -->
   <dependency>
       <groupId>com.mchange</groupId>
       <artifactId>c3p0</artifactId>
       <version>0.9.5.2</version>
   </dependency>
   
   <!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
   <dependency>
       <groupId>mysql</groupId>
       <artifactId>mysql-connector-java</artifactId>
       <version>8.0.17</version>
   </dependency>
   
   <dependency>
       <groupId>org.springframework</groupId>
       <artifactId>spring-jdbc</artifactId>
       <version>5.1.5.RELEASE</version>
   </dependency>
   ```

2. 配置数据源、JdbcTemplate（Spring提供的简化数据库操作的工具）操作数据

   ```java
   @Configuration
   @ComponentScan("com.TDVictory.tx")
   public class TxConfig {
       @Bean
       public DataSource dataSource() throws PropertyVetoException {
           ComboPooledDataSource dataSource = new ComboPooledDataSource();
           dataSource.setUser("root");
           dataSource.setPassword("vivedu");
           dataSource.setDriverClass("com.mysql.jdbc.Driver");
           dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test");
           return dataSource;
       }
   
       @Bean
       public JdbcTemplate jdbcTemplate() throws PropertyVetoException {
           JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
           return jdbcTemplate;
       }
   }
   ```

3. 配置dao类和service类，执行测试

   ```java
   @Repository
   public class UserDao {
       @Autowired
       JdbcTemplate jdbcTemplate;
   
       public void insert(String userName,int userAge){
           String sql = "INSERT INTO tbl_user(username,age) VALUES(?,?)";
           System.out.println(userName + "插入完成");
           jdbcTemplate.update(sql,userName,userAge);
       }
   }
   ```

   ```java
   @Service
   public class UserService {
       @Autowired
       UserDao userDao;
   
       public void insertUser(String userName,int userAge){
           userDao.insert(userName,userAge);        
       }
   }
   ```

4. 执行测试

   ```java
   public class IOCTest_tx {
       AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(TxConfig.class);
   
       @Test
       public void test(){
           UserService userService = annotationConfigApplicationContext.getBean(UserService.class);
           userService.insertUser("张三",21);
       }
   }
   ```

   ```
   张三插入完成
   ```

   我们可以看到数据库中插入了张三的信息。

#### 事务

我们可以看到，在Dao中，我们执行了插入数据的指令。按照SQL数据库的属性，一旦发生错误，整个事务必须为了保证原子性而回滚。现在我们为其加上这个属性。

```java
public void insert(String userName,int userAge){
    String sql = "INSERT INTO tbl_user(username,age) VALUES(?,?)";

    jdbcTemplate.update(sql,userName,userAge);
    //这里我们设置了一个错误，未添加前即使出现错误也会插入数据。
    int i = 10 / 0;
}
```

1. 使用TransactionManagement

   我们通过在配置类上标注@EnableTransactionManagement，来开启TransactionManagement的功能。

   然后往容器中注入PlatformTransactionManager组件，并设置我们的数据源

   ```java
   @EnableTransactionManagement
   @Configuration
   @ComponentScan("com.TDVictory.tx")
   public class TxConfig {
   	...
       @Bean
       public PlatformTransactionManager platformTransactionManager() throws PropertyVetoException {
           return new DataSourceTransactionManager(dataSource());
       }
   }
   ```

2. @Transaction

   我们在事务方法上标注@Transactional，这样该方法内出现异常后整个方法均会回滚。

   ```java
   @Repository
   public class UserDao {
       @Autowired
       JdbcTemplate jdbcTemplate;
   
       @Transactional
       public void insert(String userName,int userAge){
           String sql = "INSERT INTO tbl_user(username,age) VALUES(?,?)";
   
           jdbcTemplate.update(sql,userName,userAge);
           System.out.println(userName + "插入完成");
           int i = 10 / 0;
       }
   }
   ```

   测试后我们会发现控制台输出了插入完成，但是因为回滚的原因，数据库中并没有添加这个数据。

​	