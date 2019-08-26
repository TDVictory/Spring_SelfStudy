import com.TDVictory.bean.Car;
import com.TDVictory.bean.Cat;
import com.TDVictory.bean.Dog;
import com.TDVictory.bean.Person;
import com.TDVictory.config.MainConfig;
import com.TDVictory.config.MainConfig2;
import com.TDVictory.config.MainConfig3;
import com.TDVictory.imports.Color;
import com.TDVictory.imports.ColorFactoryBean;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

public class IOCTest {

    @SuppressWarnings("resource")
    @Test
    public void test01() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig2.class);
        String[] definitionNames = applicationContext.getBeanDefinitionNames();
        for (String name:definitionNames
             ) {
            System.out.println(name);
        }

        Color bean = applicationContext.getBean(Color.class);
        //System.out.println("bean的类型：" + bean.getClass());
        bean.run();
    }

    @Test
    public void test02() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig2.class);
        System.out.println("AOC容器配置完成");
        Person person1 = applicationContext.getBean(Person.class);
        Person person2 = applicationContext.getBean(Person.class);
        System.out.println(person1 == person2);
    }

    @Test
    public void test03(){
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig2.class);
        String[] names = applicationContext.getBeanNamesForType(Person.class);
        for (String name:names
             ) {
            System.out.println(name);
        }
    }

    @Test
    public void testDog(){
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig3.class);
        System.out.println("容器创建完成");
        Dog dog = applicationContext.getBean(Dog.class);
        ((AnnotationConfigApplicationContext) applicationContext).close();
    }
}
