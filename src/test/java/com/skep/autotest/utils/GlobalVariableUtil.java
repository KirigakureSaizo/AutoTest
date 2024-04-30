package com.skep.autotest.utils;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.skep.autotest.test.BaseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.regex.Matcher;

public class GlobalVariableUtil {

    public static Logger logger = LoggerFactory.getLogger(GlobalVariableUtil.class);

    /**
     * 从全局map中获取全局变量并替换
     *
     * @param parameters
     * @return
     */
    public static String substitutionGlobalVariable(String parameters) {
        if (!StringUtil.isNotNullAndEmpty(parameters)) {
            return "";
        }
        Matcher matcher = BaseCase.replaceParamPattern.matcher(parameters);
        while (matcher.find()) {
            String replaceKey = matcher.group(1).trim();  // 得到第一个匹配内容
            String value;
            // 从全局变量map中获取值
            if ("".equals(replaceKey) || !BaseCase.globalVariableMap.containsKey(replaceKey)) {
                value = null;
                Assert.fail("替换失败");  // 如果未能找到对应的值，该用例失败
            } else {
                value = BaseCase.globalVariableMap.get(replaceKey);
                // logger.info("value: " + value);
            }
            parameters = parameters.replace(matcher.group(), value);
        }
        return parameters;
    }

    /**
     * 解析全局变量，从响应内容获取值，并更新全局变量的值
     *
     * @param globalVariables
     */
    public static void saveGlobalVariable(String resp, String globalVariables) {
        if (null == resp || "".equals(resp) || null == globalVariables || "".equals(globalVariables)) {
            return;
        }
        String key, value;
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(resp);
        String[] globalVariablearr = globalVariables.split(";");
        for (String globalVariable : globalVariablearr) {
            if (StringUtil.isNotNullAndEmpty(globalVariable)) {
                key = globalVariable.split("=")[0].trim();
                value = globalVariable.split("=")[1].trim();
                String value_real = JsonPath.read(document, value);
                BaseCase.globalVariableMap.put(key, value_real);
            }
        }
    }
}
