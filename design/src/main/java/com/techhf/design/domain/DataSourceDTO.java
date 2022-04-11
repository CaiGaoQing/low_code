package com.techhf.design.domain;

import lombok.Data;


@Data
public class DataSourceDTO {

    private String id;

    private String name;

    private String appId;

    /**
     * JDBC driver org.h2.Driver
     */
    private String driver;

    /**
     * JDBC url 地址
     */
    private String url;

    /**
     * JDBC 用户名
     */
    private String username;

    /**
     * JDBC 密码
     */
    private String password;
}