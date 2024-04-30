package com.skep.autotest.utils;

public class StringUtil {

    public static boolean isNotNullAndEmpty(String s) {

        return s != null && s.trim().length() != 0;
    }
}
