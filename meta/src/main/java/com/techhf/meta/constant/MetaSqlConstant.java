package com.techhf.meta.constant;

public class MetaSqlConstant {

    /**
     * 查詢數據源SQL
     */
    public static  final  String DATASOURCE="select id , app_id as appId,name,username,password,url,driver from t_data_source";

    /**
     * 查詢數據源下面有哪些表结构
     */
    public static  final  String SCHEMA="select table_name from information_schema.tables where table_schema = '#{schema}' ";

}
