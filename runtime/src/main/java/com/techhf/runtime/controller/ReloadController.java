package com.techhf.runtime.controller;

import lombok.SneakyThrows;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reload")
@Component
@Order(value = Integer.MAX_VALUE + 2 )
public class ReloadController implements ApplicationContextAware {


    @Autowired
    private SqlSessionFactory sqlSessionFactory;


    private ApplicationContext applicationContext;



    @SneakyThrows
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
