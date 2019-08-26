package com.TDVictory.config;

import com.TDVictory.bean.Person;
import com.TDVictory.condition.LinuxCondition;
import com.TDVictory.condition.WindowCondition;
import com.TDVictory.imports.ColorFactoryBean;
import com.TDVictory.imports.MyColor;
import com.TDVictory.imports.MyImportSelector;
import org.springframework.context.annotation.*;

@Configuration
@Import({MyColor.class, MyImportSelector.class})
public class MainConfig2 {

    //@Scope(value = "prototype")
    @Lazy
    @Bean
    public Person person(){
        System.out.println("给容器中添加Person");
        return new Person("zhangsan",22);
    }

    @Bean("bill")
    @Conditional(WindowCondition.class)
    public Person person1(){
        return new Person("Bill Gates",62);
    }

    @Bean("linus")
    @Conditional(value = LinuxCondition.class)
    public Person person2(){
        return new Person("Linus",70);
    }

    @Bean
    public ColorFactoryBean colorFactoryBean(){
        return new ColorFactoryBean();
    }
}
