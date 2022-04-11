package com.techhf.meta.util;

import com.techhf.meta.meta.Column;
import com.techhf.meta.register.SchemaRegister;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class ColumnUtil {

    @Autowired
    private SchemaRegister schemaRegister;

    /**
     *  保证字段匹配，过滤 传递过来的字段在数据库不存在，防止字段找不到异常
     * @param sourceId 数据源ID
     * @param tableName 表名称
     * @param columns 列信息。字段名称
     * @return 返回数据库的列和传过来的列的交集
     */
    public List<String> diffSchema(String sourceId, String tableName, List<String> columns) {
        List<Column> schemaStructure = schemaRegister.getDefaultSchemaRegister().getSchemaStructure(sourceId, tableName);
        List<String> dbColumn = schemaStructure.parallelStream().map(column -> column.getName()).collect(Collectors.toList());
        List<String> collect = columns.stream().filter(column -> {
            if (dbColumn.contains(column)) return false;
            return true;
        }).collect(Collectors.toList());
        return collect;
    }

}
