package com.techhf.meta.config;

import com.baomidou.dynamic.datasource.provider.AbstractJdbcDataSourceProvider;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.techhf.meta.constant.MetaSqlConstant;
import com.techhf.meta.util.DataSourceUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
@Component
public class DbDynamicDataSourceProvider implements ApplicationContextAware {

    private DataSourceProperty dataSourceProperty;

    @Bean
    public DynamicDataSourceProvider jdbcDynamicDataSourceProvider() throws SQLException {
        return new AbstractJdbcDataSourceProvider(dataSourceProperty.getDriverClassName(), dataSourceProperty.getUrl(), dataSourceProperty.getUsername(), dataSourceProperty.getPassword()) {
            @Override
            protected Map<String, DataSourceProperty> executeStmt(Statement statement) throws SQLException {
                log.info("=========加载数据源开始=========");
                HashMap<String, DataSourceProperty> map = new HashMap<>();
                ResultSet rs = statement.executeQuery(MetaSqlConstant.DATASOURCE);
                while (rs.next()) {
                    String id = rs.getString("id");
                    String appId = rs.getString("appId");
                    String name = rs.getString("name");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String url = rs.getString("url");
                    String driver = rs.getString("driver");
                    DataSourceProperty property = new DataSourceProperty();
                    property.setUsername(username);
                    property.setPassword(password);
                    property.setUrl(url);
                    property.setDriverClassName(driver);
                    map.put(DataSourceUtil.getSourceId(appId,id), property);
                }
                log.info("=========加载数据源结束=========");
                return map;
            }
        };
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        DynamicDataSourceProperties dynamicDataSourceProperties = (DynamicDataSourceProperties)applicationContext.getBean(DynamicDataSourceProperties.class);
        Map<String, DataSourceProperty> datasource = dynamicDataSourceProperties.getDatasource();
        for (String key : datasource.keySet()) {
            this.dataSourceProperty = datasource.get(key);
            break;
        }
    }
}
