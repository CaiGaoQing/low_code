package com.techhf.runtime.service;

import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import sun.misc.ClassLoaderUtil;

import java.io.File;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 加载后台插件,热发布
 *
 * @author caigaoqing
 */
@Service
public class LoadPluginServiceImpl  {

    @Autowired
    private GenericApplicationContext annotationConfigWebApplicationContext;

    @Autowired
    ApplicationContext context;

    /**
     * 注册插件，注册bean,注册springmvc的 bean
     *
     * @param url      上传oss的地址
     * @param scanPath 扫描路径
     * @return 注册成功的beans
     */
    @SneakyThrows
    public List<String> register(String url, String scanPath) {
        //注册的 Bean
        List<String> beans = new ArrayList<>();
        URL[] urls = scanUrl(url);
        //新建classloader 核心
        URLClassLoader urlClassLoader = new URLClassLoader(urls, annotationConfigWebApplicationContext.getClassLoader());

        //  获取导入的jar的controller  service  dao 等类，并且创建BeanDefinition
        Set<BeanDefinition> beanDefinitions = getBeanDefinitions(urlClassLoader, scanPath);
        beanDefinitions.forEach(item -> {
            registerBean(beans, urlClassLoader, item);
            //根据beanDefinition通过BeanFactory注册bean
            annotationConfigWebApplicationContext.getDefaultListableBeanFactory().registerBeanDefinition(item.getBeanClassName(), item);
        });

        //修改BeanFactory的ClassLoader
        annotationConfigWebApplicationContext.getDefaultListableBeanFactory().setBeanClassLoader(urlClassLoader);
        //获取requestMappingHandlerMapping，用来注册HandlerMapping
        RequestMappingHandlerMapping requestMappingHandlerMapping = annotationConfigWebApplicationContext.getBean(RequestMappingHandlerMapping.class);
        beanDefinitions.forEach(item -> {
            registerController(beans, urlClassLoader, requestMappingHandlerMapping, item);
        });
        urlClassLoader.close();
        ClassLoaderUtil.releaseLoader(urlClassLoader);



        return beans;
    }


    /**
     * 卸载插件，卸载bean,卸载springmvc的 bean
     *
     * @param url      上传oss的地址
     * @param scanPath 扫描路径
     * @return 卸载成功的beans
     */
    public List<String> unregisterBean(String url, String scanPath) {
        List<String> beanList = new ArrayList<>();
        try {
            URL[] urls = scanUrl(url);
            //新建classloader 核心
            URLClassLoader urlClassLoader = new URLClassLoader(urls, annotationConfigWebApplicationContext.getClassLoader());

            //  获取导入的jar的controller  service  dao 等类，并且创建BeanDefinition
            Set<BeanDefinition> beanDefinitions = getBeanDefinitions(urlClassLoader, scanPath);
            beanDefinitions.forEach(item -> {
                //根据beanDefinition通过BeanFactory注册bean
                annotationConfigWebApplicationContext.getDefaultListableBeanFactory().removeBeanDefinition(item.getBeanClassName());
            });
            //修改BeanFactory的ClassLoader
            annotationConfigWebApplicationContext.getDefaultListableBeanFactory().setBeanClassLoader(urlClassLoader);
            //获取requestMappingHandlerMapping，用来注册HandlerMapping
            RequestMappingHandlerMapping requestMappingHandlerMapping = annotationConfigWebApplicationContext.getBean(RequestMappingHandlerMapping.class);
            beanDefinitions.forEach(item -> {
                unRegisterController(beanList, urlClassLoader, requestMappingHandlerMapping, item);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beanList;
    }

    /**
     * 注册 接口
     *
     * @param beans                        统计加载的Bean列表
     * @param urlClassLoader               url类加载器
     * @param requestMappingHandlerMapping requestMapping映射
     * @param item                         Bean定义
     */
    private void registerController(List<String> beans, URLClassLoader urlClassLoader, RequestMappingHandlerMapping requestMappingHandlerMapping, BeanDefinition item) {
        String classname = item.getBeanClassName();
        beans.add(classname);
        try {
            Class<?> c = Class.forName(classname, false, urlClassLoader);
            Controller controllerAnnotation = c.getAnnotation(Controller.class);
            RestController restControllerAnnotation = c.getAnnotation(RestController.class);
            //获取该bean 真正的创建
            Object proxy = annotationConfigWebApplicationContext.getBean(item.getBeanClassName());
            //如果此bean是Controller，则注册到RequestMappingHandlerMapping里面
            if (controllerAnnotation != null || restControllerAnnotation != null) {

                Method getMappingForMethod = ReflectionUtils.findMethod(RequestMappingHandlerMapping.class, "getMappingForMethod", Method.class, Class.class);
                getMappingForMethod.setAccessible(true);
                try {
                    Method[] methodArr = c.getMethods();
                    for (Method md : methodArr) {
                        AnnotatedType annotatedReturnType = md.getAnnotatedReturnType();
                        annotatedReturnType.getType();
                        RequestMapping aliasAnnotation = AnnotationUtils.findAnnotation(md, RequestMapping.class);
                        if (aliasAnnotation != null) {
                            //创建RequestMappingInfo
                            RequestMappingInfo mappingInfo = (RequestMappingInfo) getMappingForMethod.invoke(requestMappingHandlerMapping, md, c);
                            //注册
                            requestMappingHandlerMapping.registerMapping(mappingInfo, proxy, md);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册Bean
     *
     * @param list           列表
     * @param urlClassLoader url类加载器
     * @param item           bean定义
     */
    private void registerBean(List<String> list, URLClassLoader urlClassLoader, BeanDefinition item) {
        try {
            Class<?> c = Class.forName(item.getBeanClassName(), false, urlClassLoader);
            //处理@service指定beanName的注册别名
            if (c.getAnnotation(Service.class) != null) {
                String beanName = c.getAnnotation(Service.class).value();
                list.add("AliasBean>>" + beanName);
                annotationConfigWebApplicationContext.getDefaultListableBeanFactory().registerAlias(item.getBeanClassName(), beanName);
            }
            //处理接口注册别名
            if (c.getInterfaces().length > 0) {
                AnnotatedType[] annotatedInterfaces = c.getAnnotatedInterfaces();
                for (int i = 0; i < annotatedInterfaces.length; i++) {
                    Type type = annotatedInterfaces[i].getType();
                    String typeName = type.getTypeName();
                    list.add("AliasBean>>" + typeName);
                    annotationConfigWebApplicationContext.getDefaultListableBeanFactory().registerAlias(item.getBeanClassName(), typeName);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 统计
     *
     * @param beanList                     bean列表
     * @param urlClassLoader               url类装入器
     * @param requestMappingHandlerMapping 请求映射处理程序映射
     * @param item                         项
     */
    private void unRegisterController(List<String> beanList, URLClassLoader urlClassLoader, RequestMappingHandlerMapping requestMappingHandlerMapping, BeanDefinition item) {
        String classname = item.getBeanClassName();
        beanList.add(classname);
        try {
            Class<?> c = Class.forName(classname, false, urlClassLoader);
            Controller controllerAnnotation = c.getAnnotation(Controller.class);
            RestController restControllerAnnotation = c.getAnnotation(RestController.class);
            //获取该bean 真正的创建
            //如果此bean是Controller，则注册到RequestMappingHandlerMapping里面
            if (controllerAnnotation != null || restControllerAnnotation != null) {

                Method getMappingForMethod = ReflectionUtils.findMethod(RequestMappingHandlerMapping.class, "getMappingForMethod", Method.class, Class.class);
                getMappingForMethod.setAccessible(true);
                try {
                    Method[] methodArr = c.getMethods();
                    for (Method md : methodArr) {
                        AnnotatedType annotatedReturnType = md.getAnnotatedReturnType();
                        annotatedReturnType.getType();
                        RequestMapping aliasAnnotation = AnnotationUtils.findAnnotation(md, RequestMapping.class);
                        if (aliasAnnotation != null) {
                            //创建RequestMappingInfo
                            RequestMappingInfo mappingInfo = (RequestMappingInfo) getMappingForMethod.invoke(requestMappingHandlerMapping, md, c);
                            //卸载
                            requestMappingHandlerMapping.unregisterMapping(mappingInfo);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                urlClassLoader.close();
                ClassLoaderUtil.releaseLoader(urlClassLoader);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param url
     * @return
     * @throws MalformedURLException
     */
    public URL[] scanUrl(String url) throws MalformedURLException {
        File file = new File(FilenameUtils.getName(url));
        //查找依赖的jar包，同级目录下的lib/
        List<URL> dependencyJar = findDependencyJar(file);

        URL[] urls = dependencyJar.toArray(new URL[dependencyJar.size()]);
        return urls;
    }


    /**
     * 查看jar中类
     *
     * @param file 文件
     * @return 返回地址
     * @throws MalformedURLException
     */
    private static List<URL> findDependencyJar(File file) throws MalformedURLException {
        List<URL> list = new ArrayList<>();
        File parentFile = file.getParentFile();
        File libFile = new File(FilenameUtils.getName(file.getParent() + File.separator + "lib"));
        if (libFile.exists() && parentFile.isDirectory()) {
            for (File jar : libFile.listFiles()) {
                if (jar.isFile()
                        && jar.getName().toLowerCase().endsWith(".jar")
                ) {
                    list.add(jar.toURI().toURL());
                }
            }
        }
        list.add(file.toURI().toURL());
        return list;

    }


    /**
     * 加载BeanDefinition
     *
     * @param classLoader 类加载器
     * @param scanPath    扫描路径
     * @return 生成BeanDefinition
     * @throws Exception
     */
    public Set<BeanDefinition> getBeanDefinitions(ClassLoader classLoader, String scanPath) throws Exception {
        Set<BeanDefinition> candidates = new LinkedHashSet<>();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(classLoader);
        Resource[] resources = resourcePatternResolver.getResources("classpath*:" + scanPath + "/*.**");

        MetadataReaderFactory metadata = new SimpleMetadataReaderFactory();
        for (Resource resource : resources) {
            MetadataReader metadataReader = metadata.getMetadataReader(resource);
            ScannedGenericBeanDefinition beanDefinition = new ScannedGenericBeanDefinition(metadataReader);
            String classname = beanDefinition.getBeanClassName();
            Class<?> aClass = Class.forName(classname, false, classLoader);
            if (aClass.isInterface()) {
                continue;
            }
            beanDefinition.setResource(resource);
            beanDefinition.setSource(resource);
            candidates.add(beanDefinition);
        }
        return candidates;
    }


}
