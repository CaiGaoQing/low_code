package com.techhf.runtime.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 一切配置的入口
 * @author rongdi
 * @date 2021-03-06
 * @blog https://www.cnblogs.com/rongdi
 */
@Configuration
public class DynamicConfig implements ApplicationContextAware {


    private ApplicationContext applicationContext;

    @Value("${dynamic.jar:/}")
    private String dynamicJar;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 随便找个事件ApplicationStartedEvent，用来reload外部的jar，其实直接在moduleApplication()方法也可以做
     * 这件事，但是为了验证容器初始化后再加载扩展包还可以生效，所以故意放在了这里。
     * @return
     */
    @Bean

    public ApplicationListener applicationListener() {
        return (ApplicationListener<ApplicationStartedEvent>) event -> {
            /**
             * 加载外部扩展jar
             */
            try {
                SqlSessionFactory bean = applicationContext.getBean(SqlSessionFactory.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }


}
