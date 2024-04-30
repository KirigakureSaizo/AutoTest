package com.skep.autotest.utils;

import com.skep.autotest.model.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VariableUtil {

    public static Logger logger = LoggerFactory.getLogger(VariableUtil.class);

    // 存放变量和值的map
    public static Map<String, String> variableMap = new HashMap<>();

    // 从对象列表variableList中获取变量和值，放到map中
    public static void loadVariablesToMap(List<Variable> variableList) {
        for (Variable variable : variableList) {
            String name = variable.getName();
            String value = variable.getValue();
            variableMap.put(name, value);
        }
    }

    // 替换变量
    public static String variableSubstitution(String parameters) {
        // 获取所有变量名
        Set<String> names = variableMap.keySet();
        for (String name : names) {
            if (parameters.contains(name)) {
                parameters = parameters.replace(name, variableMap.get(name));
            }
        }
        return parameters;
    }
}
