package cn.jcyh.eaglekinglockdemo.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jogger on 2018/5/4.
 */

abstract class BaseHttpAction {
    private static final String TAG = BaseHttpAction.class.getSimpleName();
    ILockHttpRequest mHttpRequest;

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
        builder.connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS);
//                .cookieJar(new CookiesManager());//连接超时
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i(TAG, "http message:" + message);
            }
        });
//设定日志级别
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        builder.interceptors().add(httpLoggingInterceptor);
        builder.interceptors().add(new ReceivedCookiesInterceptor(context));
        builder.interceptors().add(new AddCookiesInterceptor());
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        LockRequestService lockRequestService = retrofit.create(LockRequestService.class);
        mHttpRequest = getHttpRequest(lockRequestService);
    }

    public abstract ILockHttpRequest getHttpRequest(LockRequestService lockRequestService);

    public abstract String getBaseUrl();
}
