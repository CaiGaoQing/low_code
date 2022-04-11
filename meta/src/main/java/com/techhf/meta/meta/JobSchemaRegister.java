package com.techhf.meta.meta;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.techhf.meta.register.SchemaRegister;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
@Component
public class JobSchemaRegister implements ApplicationContextAware {

    @Autowired
    private SchemaRegister schemaRegister;


    /**
     * 1 分钟 1次，加载数据源
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        DynamicRoutingDataSource dataSource = (DynamicRoutingDataSource)applicationContext.getBean(DataSource.class);
        new Timer("SchemaTimer").schedule(new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                log.debug("定时任务------加载数据源结构信息");
                schemaRegister.getDefaultSchemaRegister().init(dataSource);
            }
        }, 0,60000 * 5);
    }
}
