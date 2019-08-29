import com.TDVictory.aop.MathCalculator;
import com.TDVictory.config.MainConfigOfAOP;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AOPTest {
    AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(MainConfigOfAOP.class);
    @Test
    public void aopTest(){
        MathCalculator mathCalculator = annotationConfigApplicationContext.getBean(MathCalculator.class);
        System.out.println(mathCalculator.div(6,2));
    }
}
