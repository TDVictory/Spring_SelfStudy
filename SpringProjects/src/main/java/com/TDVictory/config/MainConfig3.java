package com.TDVictory.config;

import com.TDVictory.bean.Car;
import com.TDVictory.bean.Cat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@ComponentScan("com.TDVictory")
public class MainConfig3 {

    @Bean(initMethod = "init",destroyMethod = "destroy")
    @Scope(value = "prototype")
    public Car car(){
        return new Car();
    }

}
