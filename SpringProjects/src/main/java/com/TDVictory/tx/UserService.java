package com.TDVictory.tx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserDao userDao;

    public void insertUser(String userName,int userAge){
        userDao.insert(userName,userAge);

    }
}
