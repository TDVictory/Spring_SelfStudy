package com.TDVictory.config;

import com.TDVictory.bean.Person;
import com.TDVictory.service.BookService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

//配置类
@Configuration  //告诉Spring这是一个配置类
@ComponentScan(value = "com.TDVictory",excludeFilters = {
       @ComponentScan.Filter(type = FilterType.ANNOTATION,classes = {Controller.class}),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = BookService.class)
})
public class MainConfig {

    @Bean
    public Person person(){
        return new Person("lisi",20);
    }
}
