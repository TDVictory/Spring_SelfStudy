package com.TDVictory.config;

import com.TDVictory.bean.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = {"classpath:/Person.properties"})
@Configuration
public class MainConfig4 {
    @Bean
    public Person person(){
        return new Person();
    }
}
