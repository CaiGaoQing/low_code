//package com.hftech.meta.meta;
//
//import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
//import com.hftech.meta.register.SchemaRegister;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.BeansException;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//
//import org.springframework.stereotype.Component;
//
//import javax.sql.DataSource;
//
//
//@Component
//@Slf4j
//public class SchemaContextAware implements ApplicationContextAware {
//
//    @Autowired
//    private SchemaRegister schemaRegister;
//
//    @SneakyThrows
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        DynamicRoutingDataSource dataSource = (DynamicRoutingDataSource)applicationContext.getBean(DataSource.class);
//        schemaRegister.getDefaultSchemaRegister().init(dataSource);
//    }
//
//
//}