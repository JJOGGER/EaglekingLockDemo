package cn.jcyh.eaglekinglockdemo.http;

import android.content.Context;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import cn.jcyh.eaglekinglockdemo.bean.User;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jogger on 2018/1/10.网络请求
 */
@SuppressWarnings("unchecked")
public class HttpAction {
    private static HttpAction sHttpAction;
    private static Context sContext;
    private IHttpRequest mHttpRequest;

    private HttpAction() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS);
//                .cookieJar(new CookiesManager());//连接超时
        builder.interceptors().add(new ReceivedCookiesInterceptor(sContext));
        builder.interceptors().add(new AddCookiesInterceptor(sContext));
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(RequestService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        RequestService requestService = retrofit.create(RequestService.class);
        mHttpRequest = new CommonHttpRequest(requestService);
    }

    public static HttpAction getHttpAction(Context context) {
        sContext = context.getApplicationContext();
        if (sHttpAction == null) {
            synchronized (HttpAction.class) {
                if (sHttpAction == null) {
                    sHttpAction = new HttpAction();
                }
            }
        }
        return sHttpAction;
    }

    public void auth(String userName, String pwd, final OnHttpRequestCallback<User> listener) {
        mHttpRequest.auth(userName, pwd, listener);
    }

    public void login(String accoount, String pwd, final OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.login(accoount, pwd, listener);
    }
}
