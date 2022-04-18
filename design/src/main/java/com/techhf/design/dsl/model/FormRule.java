package com.techhf.design.dsl.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class FormRule {

    private JSONObject form;

    private JSONObject schema;
}
