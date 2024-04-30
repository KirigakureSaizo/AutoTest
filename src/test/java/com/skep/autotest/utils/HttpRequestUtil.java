package com.skep.autotest.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class HttpRequestUtil {

    // 声明为静态方法，方便调用
    public static String getRequest(String url, Map<String, String> params, String token) {
        String resp = "";
        boolean flag = true;
        Set<String> keys = params.keySet();
        for (String key : keys) {
            // 第一个参数用?连接，后面的用&连接
            if (flag) {
                url += "?" + key + "=" + params.get(key);
                flag = false;
            } else {
                url += "&" + key + "=" + params.get(key);
            }
        }
        // System.out.println(url);
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Authorization", "Bearer " + token);
        HttpClient httpClient = HttpClients.createDefault();

        try {
            HttpResponse response = httpClient.execute(httpGet);
            resp = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ((CloseableHttpClient) httpClient).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resp;
    }

    // 声明为静态方法，方便调用
    public static String postRequest(String url, Map<String, String> params, String token) {
        String resp = "";
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Authorization", "Bearer " + token);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        // 遍历map，放到basicNameValuePairs中
        Set<String> keys = params.keySet();
        for (String key : keys) {
            nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));
        }
        HttpClient httpClient = HttpClients.createDefault();
        try {
            // 将Content-Type设置为application/x-www-form-urlencoded类型
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            resp = EntityUtils.toString(httpResponse.getEntity());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ((CloseableHttpClient) httpClient).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resp;
    }

    public static String getAuthorization(String url, String userName, String password) {
        String resp = "";
        String token = "";
        HttpPost httpPost = new HttpPost(url);
        Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("username", userName);
        loginInfo.put("password", password);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        Set<String> keys = loginInfo.keySet();
        for (String key : keys) {
            nameValuePairs.add(new BasicNameValuePair(key, loginInfo.get(key)));
        }
        HttpClient httpClient = HttpClients.createDefault();
        try {
            // 将Content-Type设置为application/x-www-form-urlencoded类型
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            resp = EntityUtils.toString(httpResponse.getEntity());
            JSONObject respJson = JSONObject.parseObject(resp);
            token = respJson.getString("token");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ((CloseableHttpClient) httpClient).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return token;
    }

    public static String sendRequest(String url, String requestMethod, Map<String, String> parameters, String token) {
        String response = "";
        if ("get".equalsIgnoreCase(requestMethod)) {
            response = getRequest(url, parameters, token);
        } else if ("post".equalsIgnoreCase(requestMethod)) {
            response = postRequest(url, parameters, token);
        } else {
            response = "error request type!!!";
        }
        return response;
    }
}
