package com.techhf.runtime.dynamic;

import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapperDynamicRegister  {

    public List<String> registerMapper(ApplicationContext applicationContext, Map<String, MapperFactoryBean> beanDefinitions) {
        /**
         * 将applicationContext转换为ConfigurableApplicationContext
         */
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        /**
         * 获取bean工厂并转换为DefaultListableBeanFactory
         */
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
        ArrayList<String> beans = new ArrayList<>();
        beanDefinitions.forEach((beanName,obj) ->{
            /**
             * 如果已经存在，则销毁之后再注册
             */
            if(defaultListableBeanFactory.containsSingleton(beanName)) {
                defaultListableBeanFactory.destroySingleton(beanName);
            }
            defaultListableBeanFactory.registerSingleton(beanName,obj);
            beans.add(beanName);
        });
        return beans;
    }
}
