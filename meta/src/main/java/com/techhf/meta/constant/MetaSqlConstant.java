package com.techhf.meta.constant;

public class MetaSqlConstant {

    /**
     * 查詢數據源SQL
     */
    public static  final  String DATASOURCE="select source_id , name,user_name,pass_word,url,driver from lowcode_datasource";

    /**
     * 查詢數據源下面有哪些表结构
     */
    public static  final  String SCHEMA="select table_name from information_schema.tables where table_schema = '#{schema}' ";

}
