package com.techhf.runtime.dynamic;

import lombok.SneakyThrows;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ControllerDynamicRegister extends AbstractDynamicRegister implements DynamicRegister{

    @Override
    @SneakyThrows
    public List<String> register(GenericApplicationContext applicationContext, Set<BeanDefinition> beanDefinitions) {
        //获取requestMappingHandlerMapping，用来注册HandlerMapping
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        ArrayList<String> beans = new ArrayList<>();
        beanDefinitions.forEach(item -> {

            Class<?> className = getClassName(item, applicationContext.getClassLoader());
            Controller controllerAnnotation = className.getAnnotation(Controller.class);
            RestController restControllerAnnotation = className.getAnnotation(RestController.class);
            //获取该bean 真正的创建
            Object proxy = applicationContext.getBean(item.getBeanClassName());
            //如果此bean是Controller，则注册到RequestMappingHandlerMapping里面
            if (controllerAnnotation != null || restControllerAnnotation != null) {
                //返回注册的controller
                String beanClassName = item.getBeanClassName();
                beans.add(beanClassName);
                Method getMappingForMethod = ReflectionUtils.findMethod(RequestMappingHandlerMapping.class, "getMappingForMethod", Method.class, Class.class);
                getMappingForMethod.setAccessible(true);
                Method[] methodArr = className.getMethods();
                for (Method md : methodArr) {
                    AnnotatedType annotatedReturnType = md.getAnnotatedReturnType();
                    annotatedReturnType.getType();
                    RequestMapping aliasAnnotation = AnnotationUtils.findAnnotation(md, RequestMapping.class);
                    if (aliasAnnotation != null) {
                        //创建RequestMappingInfo
                        RequestMappingInfo mappingInfo = mappingInvoke(getMappingForMethod,requestMappingHandlerMapping, md, className);
                        //注册
                        requestMappingHandlerMapping.registerMapping(mappingInfo, proxy, md);
                    }
                }
            }
        });
        return beans;
    }


}
