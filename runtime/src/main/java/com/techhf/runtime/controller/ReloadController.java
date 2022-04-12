package com.techhf.runtime.controller;

import com.sun.webkit.network.URLs;
import com.techhf.runtime.RegisterBeanNames;
import com.techhf.runtime.core.MapperLoader;
import com.techhf.runtime.core.ModuleClassLoader;
import com.techhf.runtime.dynamic.DynamicRegisterMark;
import lombok.SneakyThrows;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reload")
@Component
@Order(value = Integer.MAX_VALUE + 2 )
public class ReloadController implements ApplicationContextAware {


    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    private GenericApplicationContext applicationContext;

    @GetMapping()
    public RegisterBeanNames get(String path) throws Exception {
        String pluginName = path.substring(path.lastIndexOf("/")+1,path.lastIndexOf("."));
        URL[] urls = new URL[]{URLs.newURL(path)};
        /**
         * 这里实际上是将spring的ApplicationContext的类加载器当成parent传给了自定义类加载器，很明自定义的子类加载器自己加载
         * 的类，parent类加载器直接是获取不到的，所以在自定义类加载器做了特殊的骚操作
         */
        ModuleClassLoader moduleClassLoader = new ModuleClassLoader(applicationContext.getClassLoader(), urls);
        /**
         * 使用模块类加载器加载url资源的jar包，直接返回类的全限定名和Class对象的映射，这些Class对象是
         * jar包里所有.class结尾的文件加载后的结果,同时mybatis的xml加载后，无奈的放入了
         * moduleClassLoader.getXmlBytesMap()，不是很优雅
         */
        Map<String, Class> classMap = moduleClassLoader.load();
        /**
         * 刷新mybatis的xml和Mapper接口资源，Mapper接口其实就是xml的namespace
         */
        Map<String, MapperFactoryBean> mapperMap = new MapperLoader().refresh(sqlSessionFactory, moduleClassLoader.getXmlBytesMap());

        //修改BeanFactory的ClassLoader
        DynamicRegisterMark dynamicRegisterMark = new DynamicRegisterMark();
        List mapper = dynamicRegisterMark.registerMapper(applicationContext, mapperMap);
        List beans = dynamicRegisterMark.registerService(applicationContext, classMap);
        List controller = dynamicRegisterMark.registerController(applicationContext, classMap);
        return RegisterBeanNames.builder().beans(beans).controller(controller).mapping(mapper).build();
    }

    @SneakyThrows
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (GenericApplicationContext)applicationContext;
    }
}
