package com.techhf.runtime.dynamic;

import lombok.SneakyThrows;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BeanDynamicRegister extends AbstractDynamicRegister  implements DynamicRegister{

    @Override
    @SneakyThrows
    public List<String> register(GenericApplicationContext applicationContext, Set<BeanDefinition> beanDefinitions) {
        ArrayList<String> beans = new ArrayList<>();
        beanDefinitions.forEach(item -> {
            Class<?> className = getClassName(item, applicationContext.getClassLoader());
            String beanClassName=item.getBeanClassName();
            //处理@service指定beanName的注册别名
            if (className.getAnnotation(Service.class) != null) {
                String beanName = className.getAnnotation(Service.class).value();
                beans.add("AliasBean>>" + beanName);
                applicationContext.getDefaultListableBeanFactory().registerAlias(beanClassName, beanName);
            }
            //处理接口注册别名
            if (className.getInterfaces().length > 0) {
                AnnotatedType[] annotatedInterfaces = className.getAnnotatedInterfaces();
                for (int i = 0; i < annotatedInterfaces.length; i++) {
                    Type type = annotatedInterfaces[i].getType();
                    String typeName = type.getTypeName();
                    beans.add("AliasBean>>" + typeName);
                    applicationContext.getDefaultListableBeanFactory().registerAlias(beanClassName, typeName);
                }
            }
            //根据beanDefinition通过BeanFactory注册bean
            applicationContext.getDefaultListableBeanFactory().registerBeanDefinition(beanClassName, item);
            beans.add(beanClassName);
        });
        return beans;
    }

}
