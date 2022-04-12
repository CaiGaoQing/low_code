package com.techhf.runtime.dynamic;

import lombok.Data;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class DynamicRegisterMark {

    private DynamicRegister beanDynamicRegister;

    private MapperDynamicRegister mapperDynamicRegister;

    private DynamicRegister controllerDynamicRegister;

    private DynamicRegister serviceDynamicRegister;

    public DynamicRegisterMark() {
        beanDynamicRegister = new BeanDynamicRegister();
        mapperDynamicRegister = new MapperDynamicRegister();
        controllerDynamicRegister = new ControllerDynamicRegister();
        serviceDynamicRegister = new ServiceDynamicRegister();
    }

    public List registerService(ApplicationContext applicationContext, Map<String,Class> classMap){
        return beanDynamicRegister.register(applicationContext,classMap);
    }

    public List registerMapper(ApplicationContext applicationContext, Map<String, MapperFactoryBean> beanDefinitions){
        return mapperDynamicRegister.registerMapper(applicationContext,beanDefinitions);
    }

    public List registerController(GenericApplicationContext applicationContext, Map<String, Class> classMap) {
        return controllerDynamicRegister.registerController((ConfigurableApplicationContext) applicationContext, classMap);
    }
}
