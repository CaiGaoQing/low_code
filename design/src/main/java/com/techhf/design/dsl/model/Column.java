package com.techhf.design.dsl.model;

import lombok.Data;

@Data
public class Column {

    private String key;

    private String name;

    private String prop;

    public Column(String key, String name, String prop) {
        this.key = key;
        this.name = name;
        this.prop = prop;
    }

    public Column() {

    }
}
