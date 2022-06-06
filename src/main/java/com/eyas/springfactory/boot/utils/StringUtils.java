package com.eyas.springfactory.boot.utils;

public class StringUtils {

    public static String lowerFirst(String str){
        char[] cs=str.toCharArray();
        cs[0]+=32;
        return String.valueOf(cs);
    }
}
