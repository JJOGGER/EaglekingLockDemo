package com.ttlock.bl.sdk.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jogger on 2018/5/4.
 */

abstract class BaseHttpAction {
    IHttpRequest mHttpRequest;

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);

        if (manager == null) {
            return false;
        }

        NetworkInfo networkinfo = manager.getActiveNetworkInfo();

        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }

        return true;
    }

    BaseHttpAction(Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS);
//                .cookieJar(new CookiesManager());//连接超时
        builder.interceptors().add(new ReceivedCookiesInterceptor(context));
        builder.interceptors().add(new AddCookiesInterceptor(context));
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        RequestService requestService = retrofit.create(RequestService.class);
        mHttpRequest = getHttpRequest(requestService);
    }

    public abstract IHttpRequest getHttpRequest(RequestService requestService);

    public abstract String getBaseUrl();
}
