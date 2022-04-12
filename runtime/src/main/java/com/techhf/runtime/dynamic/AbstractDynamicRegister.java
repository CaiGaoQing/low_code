package com.techhf.runtime.dynamic;

import com.techhf.runtime.utils.ReflectUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.naming.ResourceRef.SINGLETON;

public abstract class AbstractDynamicRegister implements DynamicRegister{


    private Set<RequestMappingInfo> extMappingInfos = new HashSet<>();

    @Override
    public List<String> register(ApplicationContext applicationContext, Map<String, Class> beanDefinitions){
        /**
         * 将applicationContext转换为ConfigurableApplicationContext
         */
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        /**
         * 获取bean工厂并转换为DefaultListableBeanFactory
         */
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
        ArrayList<String> beans = new ArrayList<>();
        for (Map.Entry<String, Class> entry : beanDefinitions.entrySet()) {
            Class<?> clazz = entry.getValue();

            String className = entry.getKey();
            //判断class对象是否带有spring的注解
            if (!isSpringBeanClass(clazz)) {
                continue;
            }
            //使用spring的BeanDefinitionBuilder将Class对象转成BeanDefinition
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
            BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
            beans.add(className);
            //设置当前bean定义对象是单利的
            beanDefinition.setScope(SINGLETON);
            defaultListableBeanFactory.registerBeanDefinition(className, beanDefinition);
        }
        return beans;
    }

    @Override
    @SneakyThrows
    public List registerController(ConfigurableApplicationContext applicationContext, Map<String, Class> classMap){

        Map<String, RequestMappingHandlerMapping> map = applicationContext.getBeanFactory().getBeansOfType(RequestMappingHandlerMapping.class);
        /**
         * 先拿到RequestMappingHandlerMapping对象
         */
        RequestMappingHandlerMapping mappingHandlerMapping = map.get("requestMappingHandlerMapping");

        /**
         * 重新注册mapping前先判断是否存在了，存在了就先unregister掉
         */
        if(!extMappingInfos.isEmpty()) {
            for(RequestMappingInfo requestMappingInfo:extMappingInfos) {
                mappingHandlerMapping.unregisterMapping(requestMappingInfo);
            }
        }

        /**
         * 获取刷新前的RequestMappingInfo
         */
        Map<RequestMappingInfo, HandlerMethod> preMappingInfoHandlerMethodMap = mappingHandlerMapping.getHandlerMethods();
        /**
         * 这里注意一定要拿到拷贝，不然刷新后内容就一致了，就没有差量了
         */
        Set<RequestMappingInfo> preRequestMappingInfoSet = new HashSet(preMappingInfoHandlerMethodMap.keySet());

        /**
         * 这里是刷新springmvc上下文
         */
        applicationContext.getBeanFactory().getBeansOfType(RequestMappingHandlerMapping.class)
                .forEach((key,value) ->{
                    value.afterPropertiesSet();
                });

        /**
         * 获取刷新后的RequestMappingInfo
         */
        Map<RequestMappingInfo, HandlerMethod> afterMappingInfoHandlerMethodMap = mappingHandlerMapping.getHandlerMethods();
        Set<RequestMappingInfo> afterRequestMappingInfoSet = afterMappingInfoHandlerMethodMap.keySet();

        /**
         * 填充差量部分RequestMappingInfo
         */
        fillSurplusRequestMappingInfos(preRequestMappingInfoSet,afterRequestMappingInfoSet);

        /**
         * 这里真的是不讲武德了，每次调用value.afterPropertiesSet();如下urlLookup都会产生重复，暂时没找到开放方法去掉重复，这里重复会导致
         * 访问的时候报错Ambiguous handler methods mapped for
         * 目标是去掉RequestMappingHandlerMapping -> RequestMappingInfoHandlerMapping -> AbstractHandlerMethodMapping
         * -> mappingRegistry -> urlLookup重复的RequestMappingInfo,这里的.getClass().getSuperclass().getSuperclass()相信会
         * 很懵逼，如果单独通过getClass().getDeclaredMethod("getMappingRegistry",new Class[]{})是无论如何都拿不到父类的非public非
         * protected方法的，因为这个方法不属于子类，只有父类才可以访问到，只有你拿得到你才有资格不讲武德的使用method.setAccessible(true)强行
         * 访问
         */
        Method method = ReflectUtil.getMethod(mappingHandlerMapping,"getMappingRegistry",new Class[]{});
        method.setAccessible(true);
        Object mappingRegistryObj = method.invoke(mappingHandlerMapping,new Object[]{});
        Field field = mappingRegistryObj.getClass().getDeclaredField("urlLookup");
        field.setAccessible(true);
        MultiValueMap<String, RequestMappingInfo> multiValueMap = (MultiValueMap)field.get(mappingRegistryObj);
        multiValueMap.forEach((key,list) -> {
            clearMultyMapping(list);
        });
        return preRequestMappingInfoSet.stream().map(RequestMappingInfo::getName).collect(Collectors.toList());
    }

    /**
     * 填充差量的RequestMappingInfo，因为已经重写过hashCode和equals方法所以可以直接用对象判断是否存在
     * @param preRequestMappingInfoSet
     * @param afterRequestMappingInfoSet
     */
    private void fillSurplusRequestMappingInfos(Set<RequestMappingInfo> preRequestMappingInfoSet,Set<RequestMappingInfo> afterRequestMappingInfoSet) {
        for(RequestMappingInfo requestMappingInfo:afterRequestMappingInfoSet) {
            if(!preRequestMappingInfoSet.contains(requestMappingInfo)) {
                extMappingInfos.add(requestMappingInfo);
            }
        }
    }

    /**
     * 简单的逻辑，删除List里重复的RequestMappingInfo，已经写了toString，直接使用mappingInfo.toString()就可以区分重复了
     * @param mappingInfos
     */
    private void clearMultyMapping(List<RequestMappingInfo> mappingInfos) {
        Set<String> containsList = new HashSet<>();
        for(Iterator<RequestMappingInfo> iter = mappingInfos.iterator();iter.hasNext();) {
            RequestMappingInfo mappingInfo = iter.next();
            String flag = mappingInfo.toString();
            if(containsList.contains(flag)) {
                iter.remove();
            } else {
                containsList.add(flag);
            }
        }
    }

    public RequestMappingInfo mappingInvoke(Method getMappingForMethod,RequestMappingHandlerMapping requestMappingHandlerMapping, Method md, Class<?> className) {
        try {
            //创建RequestMappingInfo
            return  (RequestMappingInfo) getMappingForMethod.invoke(requestMappingHandlerMapping, md, className);
        }catch (InvocationTargetException e){
            return null;
        }catch (IllegalAccessException e){
            return null;
        }
    }

    /**
     * 转换为BeanDefinition
     * @param classMap
     * @return
     */
    public Set<BeanDefinition> convert(Map<String,Class> classMap){
       HashSet<BeanDefinition> beanDefinitions = new HashSet<>();
       for (Map.Entry<String, Class> entry : classMap.entrySet()) {
           Class<?> clazz = entry.getValue();
           String className = entry.getKey();
           //判断class对象是否带有spring的注解
           if (!isSpringBeanClass(clazz)) {
                continue;
           }
           //使用spring的BeanDefinitionBuilder将Class对象转成BeanDefinition
           BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
           BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
           //设置当前bean定义对象是单利的
           beanDefinition.setScope(SINGLETON);
           //将变量首字母置小写
           String beanName = StringUtils.uncapitalize(className);
           beanName = beanName.substring(beanName.lastIndexOf(".") + 1);
           beanName = StringUtils.uncapitalize(beanName);
           beanDefinition.setBeanClassName(beanName);
           beanDefinitions.add(beanDefinition);
       }
       return beanDefinitions;
   }

    public Class<?> getClassName(BeanDefinition item, ClassLoader classLoader) {
        try {
            return Class.forName(item.getBeanClassName());
        }catch (ClassNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断class对象是否带有spring的注解
     */
    public static boolean isSpringBeanClass(Class<?> cla) {
        /**
         * 如果为空或是接口或是抽象类直接返回false
         */
        if (cla == null || cla.isInterface() || Modifier.isAbstract(cla.getModifiers())) {
            return false;
        }
        Class targetClass = cla;
        while(targetClass != null) {
            /**
             * 如果包含spring注解则返回true
             */
            if (targetClass.isAnnotationPresent(Component.class) ||
                    targetClass.isAnnotationPresent(Repository.class) ||
                    targetClass.isAnnotationPresent(Service.class) ||
                    targetClass.isAnnotationPresent(Configuration.class) ||
                    targetClass.isAnnotationPresent(Controller.class) ||
                    targetClass.isAnnotationPresent(RestController.class)) {
                return true;
            }
            targetClass = targetClass.getSuperclass();
        }
        return false;
    }

}
