package com.TDVictory.service;

import com.TDVictory.dao.BookDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class BookService {
    //@Qualifier("bookDao2")
    //@Autowired(required = false)
    @Resource(name = "bookDao2")
    private BookDao bookDao;

    public void print(){
        System.out.println(bookDao.getId());
    }
}
