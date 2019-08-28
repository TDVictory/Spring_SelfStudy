package com.TDVictory.config;

import com.TDVictory.dao.BookDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ComponentScan(value = {"com.TDVictory.dao","com.TDVictory.controller","com.TDVictory.service"})
public class MainConfig5 {
    @Primary
    @Bean("bookDao2")
    public BookDao bookDao(){
        BookDao bookDao = new BookDao();
        bookDao.setId(2);
        return bookDao;
    }
}
