package com.techhf.meta.register;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.techhf.meta.meta.Column;
import lombok.SneakyThrows;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface SchemaRegister {

    SchemaRegister getDefaultSchemaRegister();

    @SneakyThrows
    void init(DynamicRoutingDataSource dataSource) throws SQLException;

    List<String> getSchema(String sourceId);

    Map<String, List<String>> getSchema();

    List<Column> getSchemaStructure(String sourceId, String tableName);

    Map<String,List<Column>> getSchemaStructure(String sourceId);

    void register(String sourceId,String tableName);
}
