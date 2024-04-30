package com.skep.autotest.test;

import com.alibaba.fastjson.JSONObject;
import com.skep.autotest.model.Case;
import com.skep.autotest.model.Variable;
import com.skep.autotest.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;
import org.testng.annotations.Optional;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;

public class BaseCase {

    public static Logger logger = LoggerFactory.getLogger(BaseCase.class);

    public static Properties properties = new Properties();

    static {
        try {
            // 解决properties中中文乱码
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream("src\\test\\resources\\config.properties"), "GBK");
            properties.load(inputStreamReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 保存所有用例对象
    public static List<Case> cases = new ArrayList<>();

    // 存放变量对象的列表
    public static List<Variable> variables = new ArrayList<>();

    // 替换符
    public static Pattern replaceParamPattern = Pattern.compile("\\$\\{(.*?)\\}");

    // 存放全局变量的map
    public static Map<String, String> globalVariableMap = new HashMap<>();

    public static String token;

    @Parameters({"excelPath", "caseSheetName", "variableSheetName"})
    @BeforeTest
    public void readDataFromExcel(@Optional("caseData/caseData.xlsx") String excelPath, @Optional("case") String caseSheetName, @Optional("variable") String variableSheetName) {
        logger.info("excelPath: " + excelPath);
        logger.info("caseSheetName: " + caseSheetName);
        logger.info("variableSheetName: " + variableSheetName);
        cases = ExcelUtil.loadExcel(excelPath, caseSheetName, Case.class);
        variables = ExcelUtil.loadExcel(excelPath, variableSheetName, Variable.class);
        VariableUtil.loadVariablesToMap(variables);
        Set<String> keys = VariableUtil.variableMap.keySet();
        for (String key : keys) {
            System.out.println(key + "=" + VariableUtil.variableMap.get(key));
        }

        String loginUrl = "http://" + properties.getProperty("project.ip").trim() + ":" + properties.getProperty("project.port").trim() + "/bs/portal/login.html";
        token = HttpRequestUtil.getAuthorization(loginUrl, properties.getProperty("project.username"), properties.getProperty("project.password"));
    }

    @DataProvider(name = "dataFromExcel")
    public Iterator<Object[]> getCaseData() {
        List<Object[]> apiDataList = new ArrayList<>();
        for (Case caseData : cases) {
            apiDataList.add(new Object[]{caseData});
        }
        return apiDataList.iterator();
    }

    @Test(dataProvider = "dataFromExcel", timeOut = 600000)
    public void test(Case caseData) {
        // 获取对象中的数据
        String url = caseData.getUrl();
        String requestMethod = caseData.getRequestMethod();
        String headers = caseData.getHeaders();
        String parameters = caseData.getParameters();
        String globalVariables = caseData.getGlobalVariables();
        String assertFields = caseData.getAssertFields();

        logger.info("url: " + url);
        logger.info("requestMethod: " + requestMethod);
        logger.info("headers: " + headers);
        logger.info("parameters: " + parameters);
        logger.info("globalVariables: " + globalVariables);
        logger.info("assertFields: " + assertFields);

        logger.info("处理前的请求参数是：" + parameters);
        // 替换入参中的非关联参数
        parameters = VariableUtil.variableSubstitution(parameters);
        // 替换入参中的关联参数
        parameters = GlobalVariableUtil.substitutionGlobalVariable(parameters);
        logger.info("处理后的请求参数是：" + parameters);

        // 拼接url
        url = "http://" + properties.getProperty("project.ip").trim() + ":" + properties.getProperty("project.port").trim() + url;
        logger.info(url);

        String actual = null;
        JSONObject headsJsonObject = JSONObject.parseObject(headers);
        // 根据请求头判断是发送json还是非json
        if (headsJsonObject != null && "application/json".equals(headsJsonObject.getString("Content-Type"))) {
            // 解析json格式字符串为JSONObject
            JSONObject paramJsonObject = JSONObject.parseObject(parameters);

            // 请求
            actual = HttpRequestJsonUtil.sendRequest(url, requestMethod, paramJsonObject, headsJsonObject, token);
            logger.info("json请求返回结果： " + actual);

        } else {
            HashMap<String, String> params = new HashMap<>();
            // 解析json格式字符串为JSONObject
            JSONObject jsonObject = JSONObject.parseObject(parameters);
            // JSONObject转换为map
            Set<String> keys = jsonObject.keySet();
            for (String key : keys) {
                params.put(key, jsonObject.getString(key));
            }
            // 请求，获取结果
            actual = HttpRequestUtil.sendRequest(url, requestMethod, params, token);
            logger.info("k-v请求返回结果： " + actual);
        }

        // 是否需要保存全局变量
        if (StringUtil.isNotNullAndEmpty(globalVariables)) {
            logger.info("开始保存全局变量：" + globalVariables);
            GlobalVariableUtil.saveGlobalVariable(actual, globalVariables);
        }

        // 是否需要断言关键字段
        if (StringUtil.isNotNullAndEmpty(assertFields)) {
            AssertUtil.getFieldsAssertRes(actual, assertFields);
        }
    }
}
