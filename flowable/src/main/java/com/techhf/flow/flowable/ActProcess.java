package com.techhf.flow.flowable;


import org.flowable.engine.repository.*;

import java.io.InputStream;

/**
 * 流程定义封装
 * @author liuxz
 * @date 2019/08/30
 */
public interface ActProcess {
    /**
     * 创建流程部署对象
     *
     * @return
     */
    public DeploymentBuilder createDeployment();
    /**
     * 创建流程部署查询对象
     *
     * @return
     */
    public DeploymentQuery createDeploymentQuery();

    /**
     * 创建流程定义查询对象
     *
     * @return
     */
    public ProcessDefinitionQuery createProcessDefinitionQuery();

    /**
     * 部署流程定义
     *
     * @param url 流程定义文件URL
     * @return
     */
    Deployment deploy(String url);

    /**
     * 部署流程定义---通过inputstream流
     *
     * @param name     流程模板文件名字
     * @param category 流程模板文件类别
     * @param in       流程模板文件流
     * @return
     */
    Deployment deploy(String name, String category, InputStream in);

    /**
     * 部署流程定义
     *
     * @param url    流程定义文件URL
     * @param pngUrl 流程定义文件pngUrl
     * @return
     */
    Deployment deploy(String url, String pngUrl);

    /**
     * 部署流程定义
     *
     * @param url    流程定义文件URL
     * @param drlUrl 规则引擎文件URL
     * @return
     */
    Deployment deployBpmnAndDrl(String url, String drlUrl);

    /**
     * 部署流程定义
     *
     * @param url      流程定义文件URL
     * @param name     流程定义名称
     * @param category 流程定义类别
     * @return
     */
    Deployment deploy(String url, String name, String category);

    /**
     * 部署流程定义
     *
     * @param url      流程定义文件URL
     * @param pngUrl   流程定义文件pngUrl
     * @param name     流程定义标识
     * @param category 流程定义类别
     * @return
     */
    Deployment deploy(String url, String pngUrl, String name, String category);

    /**
     * 根据流程定义key，判断流程定义（模板）是否已经部署过
     *
     * @param processDefinitionKey 流程定义key（即：流程模板ID）
     * @return
     */
    boolean exist(String processDefinitionKey);

    /**
     * 根据流程定义key，查询流程定义信息
     *
     * @param processDefinitionKey 流程定义key（即：流程模板ID）
     * @return
     */
    public ProcessDefinition queryByProcessDefinitionKey(String processDefinitionKey);


}
