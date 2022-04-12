package com.techhf.runtime.dynamic;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DynamicRegister {

    List<String> register(GenericApplicationContext applicationContext, Set<BeanDefinition> beanDefinitions);

    List<String> register(ApplicationContext applicationContext, Map<String,Class> beanDefinitions);

    List registerController(ConfigurableApplicationContext applicationContext, Map<String, Class> classMap);
}
