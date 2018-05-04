//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ttlock.bl.sdk.net;

import android.text.TextUtils;

import com.ttlock.bl.sdk.util.LogUtil;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class OkHttpRequest {
    private static boolean DBG = true;
    private static final OkHttpClient client;
    private static final MediaType DEFAULT;
    private static final MediaType JSON;

    private OkHttpRequest() {
        throw new AssertionError();
    }

    private static String getParamUrl(Map<String, String> params) {
        if(params != null && !params.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            Iterator var2 = params.keySet().iterator();

            while(var2.hasNext()) {
                String key = (String)var2.next();
                sb.append(key).append('=').append((String)params.get(key)).append('&');
            }

            return sb.substring(0, sb.length() - 1);
        } else {
            return "";
        }
    }

    public static String get(String url) {
        return get(url, (Map)null);
    }

    public static String get(String url, Map<String, String> params) {
        String paramUrl = getParamUrl(params);
        String newUrl = TextUtils.isEmpty(paramUrl)?url:url + "?" + paramUrl;
        Builder requestBuilder = (new Builder()).url(newUrl);
        Request request = requestBuilder.build();

        try {
            Response response = client.newCall(request).execute();
            if(!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            } else {
                return response.body().string();
            }
        } catch (IOException var8) {
            var8.printStackTrace();
            return "";
        }
    }

    public static String sendPost(String url, Map<String, String> params) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if(params != null) {
            Iterator body = params.keySet().iterator();

            while(body.hasNext()) {
                String requestBuilder = (String)body.next();
                String request = (String)params.get(requestBuilder);
                if(request == null) {
                    request = "";
                }

                LogUtil.d(String.format("%s:%s", new Object[]{requestBuilder, request}), DBG);
                formBodyBuilder.add(requestBuilder, request);
            }
        }

        FormBody body1 = formBodyBuilder.build();
        Builder requestBuilder1 = (new Builder()).url(url).post(body1);
        Request request1 = requestBuilder1.build();

        try {
            Response response = client.newCall(request1).execute();
            if(!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            } else {
                String e = response.body().string();
                LogUtil.d("responseData:" + e, DBG);
                return e;
            }
        } catch (IOException var8) {
            var8.printStackTrace();
            return "";
        }
    }

    public static String sendPost(String url, String params) {
        RequestBody body = RequestBody.create(DEFAULT, params);
        Builder requestBuilder = (new Builder()).url(url).post(body);
        Request request = requestBuilder.build();

        try {
            Response response = client.newCall(request).execute();
            if(!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            } else {
                return response.body().string();
            }
        } catch (IOException var7) {
            var7.printStackTrace();
            return "";
        }
    }

    static {
        client = (new OkHttpClient.Builder()).readTimeout(10L, TimeUnit.SECONDS).build();
        DEFAULT = MediaType.parse("application/x-www-form-urlencoded");
        JSON = MediaType.parse("application/json; charset=utf-8");
    }
}
