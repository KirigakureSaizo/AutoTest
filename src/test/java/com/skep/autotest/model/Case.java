package com.skep.autotest.model;

import lombok.Data;

/**
 * 把excel中数据读取出来，并封装到对象中，这样注入测试数据到测试方法的时候，一次注入一个对象，而不用写很多参数，
 * 另外，获取测试数据（比如url等），直接通过对象的get方法就可以很方便的获取到
 */

@Data
public class Case {

    private String caseId;
    private String api;
    private String description;
    private String requestMethod;
    private String url;
    private String headers;
    private String parameters;
    private String globalVariables;
    private String assertFields;
}
