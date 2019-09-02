import com.TDVictory.tx.TxConfig;
import com.TDVictory.tx.UserService;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class IOCTest_tx {
    AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(TxConfig.class);

    @Test
    public void test(){
        UserService userService = annotationConfigApplicationContext.getBean(UserService.class);
        userService.insertUser("李四",21);
    }
}
