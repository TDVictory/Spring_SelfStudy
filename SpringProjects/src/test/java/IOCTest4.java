import com.TDVictory.bean.Person;
import com.TDVictory.config.MainConfig4;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class IOCTest4 {
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig4.class);

    @Test
    public void test01(){
        printBeans(applicationContext);
        System.out.println("======================================");
        Person person = applicationContext.getBean(Person.class);
        System.out.println(person.getName() + "===>" + person.getAge());
        System.out.println(person.getNickName());
    }

    public void printBeans(ApplicationContext applicationContext){
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanName:beanDefinitionNames
             ) {
            System.out.println(beanName);
        }
    }
}
