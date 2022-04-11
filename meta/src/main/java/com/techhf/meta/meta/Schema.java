package com.techhf.meta.meta;


import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Schema {

    /**
     * 数据源 和 表名称对应关系
     */
    public static Map<String, List<String>> tables = new  ConcurrentHashMap<String,List<String>>();

    /**
     * 数据源Id，表，表结构
     */
    private static Map<String, Map<String,List<Column>>> tableStructure = new  ConcurrentHashMap<String, Map<String,List<Column>>>();

    /**
     * 获取数据源和表对应关系
     * @return
     */
    public static Map<String, List<String>> getSchema(){
        return tables;
    }

    /**
     * 获取数据源下面的表名称
     * @param sourceId 数据源ID
     * @return  表名称集合
     */
    public static List<String> getSchema(String sourceId){
        return tables.get(sourceId);
    }

    /**
     *  注册表
     * @param sourceId 数据源Id
     * @param tableName 表名称
     */
    public static void register(String sourceId,String tableName){
        List<String> tableNames = tables.get(sourceId);
        tableNames.add(tableName);
        tables.put(sourceId,tableNames);
    }

    /**
     *  注册表
     * @param sourceId 数据源Id
     * @param tableNames 表名称
     */
    private static void initSchema(String sourceId,List<String> tableNames,Map<String, Map<String,List<Column>>> tableStructure){
        tables.put(sourceId,tableNames);
        tableStructure.putAll(tableStructure);
    }

    /**
     * 初始化
     * @param dataSource 数据源
     * @throws SQLException SQL异常
     */
    public static void init(DynamicRoutingDataSource dataSource) throws SQLException {
        for (String sourceId : dataSource.getDataSources().keySet()) {

            Map<String, Map<String, List<Column>>> schemaStructure = new HashMap<>();

            DatabaseMetaData metaData = dataSource.getDataSources().get(sourceId).getConnection().getMetaData();
            ResultSet tableRet = metaData.getTables(null, "%", "%",new String[]{"TABLE"});
            ArrayList<String> tables = new ArrayList<>();

            while (tableRet.next()) {

                String tableName = (String) tableRet.getObject("TABLE_NAME");
                tables.add(tableName);
                //检索数据库中的列
                Map<String, List<Column>> column = getColumn(metaData, tableName);
                log.debug("加载数据源:{},表结构{},信息为：{}",sourceId,tableName,column);
                if (schemaStructure.get(sourceId)!=null){
                    schemaStructure.get(sourceId).putAll(column);
                }else{
                    schemaStructure.put(sourceId,column);
                }

            }
            Schema.initSchema(sourceId,tables,schemaStructure);
        }

    }

    /**
     *  获取表和表结构的对应信息
     * @param metaData 数据源信息
     * @param tableName 表名称
     * @return 表和表结构的对应信息
     * @throws SQLException SQL异常
     */
    private static Map<String, List<Column>> getColumn(DatabaseMetaData metaData, String tableName) throws SQLException {
        ResultSet columns = metaData.getColumns(null, null, tableName, null);
        Map<String, List<Column>> tableColumn = new HashMap<>();
        ArrayList<Column> columnArrayList = new ArrayList<>();
        //打印列名称和大小
        while (columns.next()){
            String columnName = columns.getString("COLUMN_NAME");
            long columnSize = columns.getLong("COLUMN_SIZE");
            String dataType = columns.getString("DATA_TYPE");
            String remarks = columns.getString("REMARKS");
            columnArrayList.add(Column.builder().name(columnName).length(columnSize).type(dataType).remarks(remarks).build());
        }
        tableColumn.put(tableName,columnArrayList);
        return tableColumn;
    }

    /**
     *  移除表
     * @param sourceId 数据源Id
     * @param tableNames 表名称集合
     */
    public static void unRegister(String sourceId,List<String> tableNames){
        List<String> table = tables.get(sourceId);
        Iterator<String> it = table.iterator();
        while(it.hasNext()){
            String value = it.next();
            if(tableNames.contains(value)){
                it.remove();
            }
        }
        tables.put(sourceId,tableNames);
    }


    public static List<Column> getSchemaStructure(String sourceId, String tableName) {
        Map<String, List<Column>> tableColumn = tableStructure.get(sourceId);
        if (tableColumn == null) return new ArrayList<>();
        return tableColumn.get(tableName);
    }

    public static Map<String, List<Column>> getSchemaStructure(String sourceId) {
        return tableStructure.get(sourceId);
    }

}
