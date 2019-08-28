package com.TDVictory.dao;

import org.springframework.stereotype.Repository;

@Repository
public class BookDao {
    private int id = 1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
