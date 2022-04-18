package com.techhf.design.domain.basics;

import lombok.Data;

import java.util.List;

@Data
public class NodeSchema {

    private String type;

    private List<BasicsNode> properties;

    private String XDesignableId;
}
