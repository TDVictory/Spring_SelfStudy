import com.TDVictory.bean.Person;
import com.TDVictory.config.MainConfig5;
import com.TDVictory.config.MainConfig6;
import com.TDVictory.service.BookService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class IOCTest6 {


    @Test
    public void test01(){
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.getEnvironment().setActiveProfiles("test");
        applicationContext.register(MainConfig6.class);
        applicationContext.refresh();
        printBeans(applicationContext);
    }

    public void printBeans(ApplicationContext applicationContext){
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanName:beanDefinitionNames
        ) {
            System.out.println(beanName);
        }
    }
}
