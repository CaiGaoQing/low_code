package com.techhf.design.dsl;

import com.alibaba.fastjson.JSONObject;
import com.techhf.design.base.FormSchema;
import com.techhf.design.dsl.model.FormInfo;
import com.techhf.design.dsl.model.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public abstract class AbstractResolve extends Resolver{

    /**
     * json信息
     */
    public Schema schema;

    /**
     * 获取约束信息 json
     * @return 约束信息 json
     */
    abstract Schema getSchema();

    /**
     * 获取表单信息
     * @return 表单信息
     */
    abstract FormInfo getFormInfo();

    /**
     * 节点转换为class对象，
     * @param node 节点对象
     * @param clazz 要转的对象
     * @return 对象
     */
    abstract  <T> T nodeParseClass(JSONObject node,Class<T> clazz);

    /**
     * 转换key 比如 原来每个节点都有一个key叫{"x-name":'1'},经过转换后为{"x_name":'1'}
     * 传递的参数位{"x-name","x_name"}
     * @param transformKey key为旧的，value为新的key
     * @return Schema 定义信息
     */
    abstract DefaultJsonResolver transformKey(Map<String,String> transformKey);

    abstract Schema transformKey(Schema data,Map<String, String> transformKey);

    /**
     * class对象转换为节点
     * @param data 节点对象
     * @return 对象
     */
    abstract  JSONObject classParseNode(Object data);

    /**
     * 获取节点数组转为class对象，
     * @param clazz 要转的对象
     * @return 对象
     */
    abstract <T> List<T> getNode(Class<T> clazz);

    /**
     * 添加表单字段
     * @param key 字段key
     * @param value 字段Value
     * @return 返回添加后的json
     */
    abstract Schema addFormKey(String key,Object value);

    /**
     * 修改列字段
     * @param key 字段key
     * @return 返回添加后的json
     */
    abstract Schema updateSchemaKey(String key,Object value);

    /**
     * 每一个节点上都添加字段
     * @param key 字段key
     * @param value 字段Value
     * @return 返回添加后的json
     */
    abstract Schema addNodeKey(String key,Object value);

    /**
     * 删除每个节点的key
     * @param key 字段key
     * @return 返回添加后的json
     */
    abstract Schema removeNodeKey(String key);

}
