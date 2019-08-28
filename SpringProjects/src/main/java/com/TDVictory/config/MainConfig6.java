package com.TDVictory.config;

import com.TDVictory.bean.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
@Profile("test")
@Configuration
public class MainConfig6 {

    @Bean
    public Person person(){
        return new Person();
    }
}
