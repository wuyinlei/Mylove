package com.example.ruolan.letgo.http;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.example.ruolan.letgo.application.CniaoApplication;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by ruolan on 2015/11/12.
 */
public class OkHttpHelper {

    private static final int TOKEN_MISSING = 401; //token丢失
    private static final int TOKEN_ERROR = 402; //token错误
    private static final int TOKEN_EXPIRE = 403; //token失效


    public static final String TAG = "OkHttpHelper";

    //这里使用单例模式，静态私有化
    private static OkHttpHelper mInstance;
    private OkHttpClient mHttpClient;
    private Gson mGson;

    private Handler mHandler;


    //初始化OkHttpHelper初值
    static {
        mInstance = new OkHttpHelper();
    }

    //构造函数私有化
    private OkHttpHelper() {

        //对mHttpClient的一些简单的设置
        mHttpClient = new OkHttpClient();
        //连接主机超时
        mHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        //从主机读取数据超时
        mHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
        //写出数据超时
        mHttpClient.setWriteTimeout(30, TimeUnit.SECONDS);

        mGson = new Gson();

        mHandler = new Handler(Looper.getMainLooper());

    }


    /**
     * 对外提供一个公开的静态的实例方法
     *
     * @return
     */
    public static OkHttpHelper getInstance() {
        return mInstance;
    }


    /**
     * Get方式
     *
     * @param url
     * @param callback
     */
    public void get(String url, Map<String, String> param, BaseCallback callback) {


        Request request = buildGetRequest(url, param);

        request(request, callback);

    }

    public void get(String url, BaseCallback callback) {
        get(url, null, callback);
    }


    /**
     * Post方式
     *
     * @param url
     * @param param
     * @param callback
     */
    public void post(String url, Map<String, String> param, BaseCallback callback) {

        Request request = buildPostRequest(url, param);
        request(request, callback);
    }


    /**
     * request请求，在这里实现了自定义的calllback方法
     *
     * @param request
     * @param callback
     */
    public void request(final Request request, final BaseCallback callback) {

        callback.onBeforeRequest(request);

        mHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                callback.onFailure(request, e);

            }

            @Override
            public void onResponse(Response response) throws IOException {

                //不管成功失败都调用
                callback.onResponse(response);

                if (response.isSuccessful()) {

                    String resultStr = response.body().string();

                    // Log.d(TAG, "result=" + resultStr);

                    if (callback.mType == String.class) {
                        callbackSuccess(callback, response, resultStr);
                    } else {
                        try {
                            Object obj = mGson.fromJson(resultStr, callback.mType);
                            callbackSuccess(callback, response, obj);
                        } catch (com.google.gson.JsonParseException e) { // Json解析的错误
                            callback.onError(response, response.code(), e);
                        }
                    }
                } else if (response.code() == TOKEN_MISSING || response.code() == TOKEN_ERROR || response.code() == TOKEN_EXPIRE) {
                        callbackTokenError(callback,response);
                } else {
                    callbackError(callback, response, null);
                }

            }
        });


    }

    private void callbackTokenError(final BaseCallback callback, final Response response) {
        /**
         * 用了handler异步方法，要不然就会报错的
         */
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onTokenError(response,response.code());
            }
        });
    }

    /**
     * @param callback
     * @param response
     * @param obj
     */
    private void callbackSuccess(final BaseCallback callback, final Response response, final Object obj) {

        /**
         * 用了handler异步方法，要不然就会报错的
         */
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(response, obj);
            }
        });
    }


    /**
     * 请求失败的时候调用
     *
     * @param callback
     * @param response
     * @param e
     */
    private void callbackError(final BaseCallback callback, final Response response, final Exception e) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(response, response.code(), e);
            }
        });
    }


    private Request buildPostRequest(String url, Map<String, String> params) {

        return buildRequest(url, HttpMethodType.POST, params);
    }

    private Request buildGetRequest(String url, Map<String, String> param) {

        return buildRequest(url, HttpMethodType.GET, param);
    }

    private Request buildRequest(String url, HttpMethodType methodType, Map<String, String> params) {


        Request.Builder builder = new Request.Builder()
                .url(url);

        if (methodType == HttpMethodType.POST) {
            RequestBody body = builderFormData(params);
            builder.post(body);
        } else if (methodType == HttpMethodType.GET) {
            url = buildUrlParams(url, params);
            builder.url(url);
            builder.get();
        }
        return builder.build();
    }

    /**
     * 创建参数，也就是url地址
     * @param url
     * @param param
     * @return
     */
    private String buildUrlParams(String url, Map<String, String> param) {
        if (param == null) {
            param = new HashMap<>(1);
        }
        String token = CniaoApplication.getInstance().getToken();
        if (!TextUtils.isEmpty(token))
            param.put("token", token);

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0, s.length() - 1);
        }
        if (url.indexOf("?") > 0) {
            url = url + "&" + s;
        } else {
            url = url + "?" + s;
        }
        return url;
    }


    private RequestBody builderFormData(Map<String, String> params) {


        FormEncodingBuilder builder = new FormEncodingBuilder();

        if (params != null) {

            for (Map.Entry<String, String> entry : params.entrySet()) {

                builder.add(entry.getKey(), entry.getValue());
            }
            String token = CniaoApplication.getInstance().getToken();
            if (!TextUtils.isEmpty(token)) {
                builder.add("token", token);
            }

        }

        return builder.build();

    }


    /**
     * 枚举的两个类型，一个GET，一个POST
     */
    enum HttpMethodType {

        GET,
        POST,

    }

}
