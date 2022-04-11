package com.techhf.design.dsl;

import com.alibaba.fastjson.JSONObject;

import com.techhf.design.dsl.model.Column;
import com.techhf.design.dsl.model.FormInfo;
import com.techhf.design.dsl.model.Schema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * 默认的json解析器,
 * @getFormInfo 返回一个FormInfo对象，获取表单信息
 * @getSchema 返回JSON表单信息
 * @getColumn 返回表单的所有字段信息
 *
 */
@Data
@Slf4j
public class DefaultJsonResolver extends AbstractResolve{

    private final static String propertiesKey ="properties";

    /**
     * json信息
     */
    private Schema schema;

   public DefaultJsonResolver(Schema schema) {
      this.schema=schema;
   }

    public DefaultJsonResolver(String schemaInfo) {
        try {
            schema = JSONObject.parseObject(schemaInfo, Schema.class);
        }catch (Exception e){
            log.error("解析Json Schema 失败 请检查格式:{}",e.getMessage());
        }

    }

    /**
     * 获取约束信息 json
     * @return 约束信息 json
     */
    public Schema getSchema(){
      return schema;
    }

    /**
     * 获取表单的所有列
     * @return  表单的所有列
     */
    public List<Column> getColumn() {
        JSONObject schema = this.schema.getSchema();
        JSONObject properties = schema.getJSONObject("properties");
        Set<String> keys = properties.keySet();
        ArrayList<Column> columns = new ArrayList<>();
        getKeys(properties,keys,columns);
        return columns;
    }

    /**
     * 获取表单信息
     * @return 表单信息
     */
    public FormInfo getFormInfo(){
        JSONObject schema = this.schema.getSchema();
        JSONObject form = schema.getJSONObject("form");
        return JSONObject.parseObject(form.toJSONString(),FormInfo.class);
    }

    /**
     * 添加列字段
     * @param key 字段key
     * @param value 字段Value
     * @return 返回添加后的json
     */
    public Schema addSchemaKeyValue(String key,Object value){
        JSONObject schema = this.schema.getSchema();
        JSONObject properties = schema.getJSONObject(propertiesKey);
        //递归添加列字段属性
        addRecurrenceKeyValue(properties,key,value);
        schema.put(propertiesKey,properties);
        this.schema.setSchema(schema);
        return  this.schema;
    }

    /**
     * 删除列字段
     * @param key 字段key
     * @return 返回添加后的json
     */
    public Schema removeSchemaKey(String key){
        JSONObject schema = this.schema.getSchema();
        JSONObject properties = schema.getJSONObject(propertiesKey);
        //递归添加列字段属性
        removeRecurrenceKey(properties,key);
        schema.put(propertiesKey,properties);
        this.schema.setSchema(schema);
        return this.schema;
    }

    /**
     * 修改列字段
     * @param key 字段key
     * @return 返回添加后的json
     */
    public Schema updateSchemaKey(String key,Object value){
        //相同的key 覆盖值
        return this.addSchemaKeyValue(key,value);
    }

    /**
     * 添加表单字段
     * @param key 字段key
     * @param value 字段Value
     * @return 返回添加后的json
     */
    public Schema addFormKeyValue(String key,Object value){
        JSONObject formInfo = this.schema.getForm();
        formInfo.put(key,value);
        this.schema.setForm(formInfo);
        return this.schema;
    }

    /**
     * 递归删除指定的key列字段属性
     * @param properties json属性
     * @param field  要删除的key
     */
    private static void removeRecurrenceKey(JSONObject properties,String field){
        for (String key : properties.keySet()) {
            JSONObject node = properties.getJSONObject(key);
            if (node==null){
                continue;
            }
            if (node.getJSONObject(propertiesKey) != null) {
                removeRecurrenceKey(node.getJSONObject(propertiesKey), field);
            }
            node.remove(field);
        }
    }

    /**
     * 递归添加列字段属性
     * @param properties json属性
     * @param field  json属性下面的所有key
     * @Param value 属性值 可以是字符串可以是json可以是数组
     */
    private static void addRecurrenceKeyValue(JSONObject properties,String field,Object value){
        for (String key : properties.keySet()) {
            JSONObject node = properties.getJSONObject(key);
            if (node==null){
                continue;
            }
            if (node.getJSONObject(propertiesKey) != null) {
                addRecurrenceKeyValue(node.getJSONObject(propertiesKey), field,value);
            }
            node.put(field,value);
        }
    }

    /**
     *  递归获取字段名称
     * @param properties json属性
     * @param keys  json属性下面的所有key
     * @param columns  解析出来的列属性
     */
    private static void getKeys(JSONObject properties,Set<String> keys,ArrayList<Column> columns) {
        for (String key : keys) {
            JSONObject node = properties.getJSONObject(key);
            if (node==null){
               continue;
            }
            if ("void".equals(node.getString("type")) ){
                if (node.getJSONObject(propertiesKey) != null) {
                    Set<String> ch = node.getJSONObject(propertiesKey).keySet();
                    getKeys(node.getJSONObject(propertiesKey), ch,columns);
                }
                continue;
            }
            columns.add(new Column(key,null,null));
        }
    }

    public static void main(String[] args) {
        String json="{\"form\":{\"labelCol\":6,\"wrapperCol\":12},\"schema\":{\"type\":\"object\",\"properties\":{\"9tbndobkfav\":{\"type\":\"number\",\"title\":\"Rate\",\"x-decorator\":\"FormItem\",\"x-component\":\"Rate\",\"x-validator\":[],\"x-component-props\":{},\"x-decorator-props\":{},\"x-designable-id\":\"9tbndobkfav\",\"x-index\":0},\"9e64xxuipyv\":{\"title\":\"Password\",\"x-decorator\":\"FormItem\",\"x-component\":\"Password\",\"x-validator\":[],\"x-component-props\":{},\"x-decorator-props\":{},\"x-designable-id\":\"9e64xxuipyv\",\"x-index\":1},\"e6srgv2k3wi\":{\"title\":\"Transfer\",\"x-decorator\":\"FormItem\",\"x-component\":\"Transfer\",\"x-validator\":[],\"x-component-props\":{},\"x-decorator-props\":{},\"x-designable-id\":\"e6srgv2k3wi\",\"x-index\":2},\"ta2ezeiklk7\":{\"type\":\"Array<object>\",\"title\":\"Upload\",\"x-decorator\":\"FormItem\",\"x-component\":\"Upload\",\"x-component-props\":{\"textContent\":\"Upload\"},\"x-validator\":[],\"x-decorator-props\":{},\"x-designable-id\":\"ta2ezeiklk7\",\"x-index\":3},\"qfs9rs2gjt7\":{\"type\":\"boolean\",\"title\":\"Switch\",\"x-decorator\":\"FormItem\",\"x-component\":\"Switch\",\"x-validator\":[],\"x-component-props\":{},\"x-decorator-props\":{},\"x-designable-id\":\"qfs9rs2gjt7\",\"x-index\":4},\"qiv2r43tvad\":{\"type\":\"string[]\",\"title\":\"DateRangePicker\",\"x-decorator\":\"FormItem\",\"x-component\":\"DatePicker.RangePicker\",\"x-validator\":[],\"x-component-props\":{},\"x-decorator-props\":{},\"x-designable-id\":\"qiv2r43tvad\",\"x-index\":5},\"u20wpfxwmjc\":{\"type\":\"void\",\"x-component\":\"Card\",\"x-component-props\":{\"title\":\"Title\"},\"x-designable-id\":\"u20wpfxwmjc\",\"properties\":{\"xt983k5rof1\":{\"type\":\"string\",\"title\":\"Input\",\"x-decorator\":\"FormItem\",\"x-component\":\"Input\",\"x-validator\":[],\"x-component-props\":{},\"x-decorator-props\":{},\"x-designable-id\":\"xt983k5rof1\",\"x-index\":0}},\"x-index\":6}},\"x-designable-id\":\"mzeanr6wdc5\"}}";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("test","111");
        jsonObject.put("test","222");
        Schema schema = new DefaultJsonResolver(json).addSchemaKeyValue("test", jsonObject);
        System.out.println(JSONObject.toJSONString(schema));
    }
}
