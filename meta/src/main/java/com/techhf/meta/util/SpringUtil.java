package com.techhf.meta.util;
 
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
 
/**
 * @author LemonZuo
 * @create 2020-04-28 22:17
 */
@Component
public class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext context;
 
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
 
    public static void set(ApplicationContext applicationContext) {
        context = applicationContext;
    }
 
    /**
     * 通过字节码获取
     * @param beanClass
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }
 
    /**
     * 通过BeanName获取
     * @param beanName
     * @param <T>
     * @return
     */
    public static <T> T getBean(String beanName) {
        return (T) context.getBean(beanName);
    }
 
    /**
     * 通过beanName和字节码获取
     * @param name
     * @param beanClass
     * @param <T>
     * @return
     */
    public static <T> T getBean(String name, Class<T> beanClass) {
        return context.getBean(name, beanClass);
    }
}