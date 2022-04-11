package com.techhf.meta.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeConvertCheckUtil {

    private static Map<String, List<String>> TYPE_MAPPING=new HashMap<String,List<String>>();

    static {
        //varchar 可以穿换成什么类型
        TYPE_MAPPING.put("VARCHAR",new ArrayList<String>(){
            {
                add("VARCHAR");
                add("BLOB");
                add("TEXT");
                add("LONGTEXT");
            }
        });

    }

}
