import com.TDVictory.config.MainConfig5;
import com.TDVictory.service.BookService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class IOCTest5 {
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig5.class);

    @Test
    public void test01(){
        System.out.println("======================================");
        BookService bookService = applicationContext.getBean(BookService.class);
        bookService.print();
    }


}
