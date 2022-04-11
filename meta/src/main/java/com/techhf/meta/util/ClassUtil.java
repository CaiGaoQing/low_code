package com.techhf.meta.util;

public class ClassUtil {

    public static Boolean classIsExit(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
