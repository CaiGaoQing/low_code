package com.techhf.meta.register;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.techhf.meta.meta.Column;
import com.techhf.meta.meta.Schema;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class InMemorySchemaRegister extends AbstractSchemaRegister {

    @Override
    @SneakyThrows
    public void init(DynamicRoutingDataSource dataSource)  {
        Schema.init(dataSource);
    }

    @Override
    public List<String> getSchema(String sourceId) {
        return Schema.getSchema(sourceId);
    }

    @Override
    public Map<String, List<String>> getSchema() {
        return Schema.getSchema();
    }

    @Override
    public List<Column> getSchemaStructure(String sourceId, String tableName) {
        return Schema.getSchemaStructure(sourceId,tableName);
    }

    @Override
    public Map<String, List<Column>> getSchemaStructure(String sourceId) {
        return Schema.getSchemaStructure(sourceId);
    }

    @Override
    public void register(String sourceId, String tableName) {
        Schema.register(sourceId,tableName);
    }
}
