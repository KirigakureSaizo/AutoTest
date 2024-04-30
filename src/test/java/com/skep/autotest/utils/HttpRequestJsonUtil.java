package com.skep.autotest.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Set;

public class HttpRequestJsonUtil {

    // 声明为静态方法，方便调用
    public static String postRequest(String url, JSONObject jsonObject, JSONObject headers, String token) {
        String resp = "";
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Authorization", "Bearer " + token);
        // 通过形参设置请求头
        Set<String> headerkeys = headers.keySet();
        for (String headerkey : headerkeys) {
            httpPost.addHeader(headerkey.trim(), headers.getString(headerkey).trim());
        }
        // 发送 json 类型数据
        httpPost.setEntity(new StringEntity(jsonObject.toString(), "UTF-8"));
        // 创建可供关闭的发包客户端
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 发送请求
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpPost);
            // System.out.println("状态码：" + httpResponse.getStatusLine().getStatusCode());
            resp = EntityUtils.toString(httpResponse.getEntity());
            // res = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resp;
    }

    public static String sendRequest(String url, String requestMethod, JSONObject jsonObject, JSONObject headers, String token) {
        String response = "";
        if ("post".equalsIgnoreCase(requestMethod)) {
            response = postRequest(url, jsonObject, headers, token);
        } else {
            response = "error request type!!!";
        }
        return response;
    }
}
