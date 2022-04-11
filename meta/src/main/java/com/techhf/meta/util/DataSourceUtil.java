package com.techhf.meta.util;

public class DataSourceUtil {

    public static String getSourceId(String appId,String sourceId){
        return appId.concat("-").concat(sourceId);
    }
}
