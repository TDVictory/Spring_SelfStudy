package com.TDVictory.tx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Transactional
    public void insert(String userName,int userAge){
        String sql = "INSERT INTO tbl_user(username,age) VALUES(?,?)";

        jdbcTemplate.update(sql,userName,userAge);
        System.out.println(userName + "插入完成");

    }


}
