package com.TDVictory.bean;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class Cat implements InitializingBean, DisposableBean {
    public Cat() {
        System.out.println("Cat Constuctor");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("Cat Destroy");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Cat afterPropertiesSet");
    }
}
